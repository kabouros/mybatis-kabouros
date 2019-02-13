/**
 * MIT License
 * Copyright (c) 2018-2019 jiangcihuo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kabouros.mybatis.core.builder;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.kabouros.mybatis.api.domain.Page;
import com.kabouros.mybatis.api.domain.Pageable;
import com.kabouros.mybatis.core.dialect.Dialect;


/**
 * @see org.apache.ibatis.builder.xml.XMLMapperBuilder
 * @author Clinton Begin
 * @author Kazuki Shimizu
 * @author JIANG
 */
public class XMLMapperBuilder extends BaseBuilder {

	private final Dialect dialect;
	private final XPathParser parser;
	private final MapperBuilderAssistant builderAssistant;
	private final Map<String, XNode> sqlFragments;
	private final String resource;

	public XMLMapperBuilder(InputStream inputStream, 
			                Configuration configuration, 
			                String resource,Map<String, XNode> sqlFragments, 
			                String namespace,
			                Dialect dialect) {
		this(inputStream, configuration, resource, sqlFragments,dialect);
		this.builderAssistant.setCurrentNamespace(namespace);
	}

	public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource,Map<String, XNode> sqlFragments,Dialect dialect) {
			
		this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()),configuration, resource, sqlFragments,dialect);
	}

	private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource,Map<String, XNode> sqlFragments,Dialect dialect) {
		super(configuration);
		this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
		this.parser = parser;
		this.sqlFragments = sqlFragments;
		this.resource = resource;
		this.dialect = dialect;
	}

	public void parse() {
		if (!configuration.isResourceLoaded(resource)) {
			configurationElement(parser.evalNode("/mapper"));
			configuration.addLoadedResource(resource);
			bindMapperForNamespace();
		}

		parsePendingResultMaps();
		parsePendingCacheRefs();
		parsePendingStatements();
	}

	public XNode getSqlFragment(String refid) {
		return sqlFragments.get(refid);
	}

	private void configurationElement(XNode context) {
		try {
			String namespace = context.getStringAttribute("namespace");
			if (namespace == null || namespace.equals("")) {
				throw new BuilderException("Mapper's namespace cannot be empty");
			}
			builderAssistant.setCurrentNamespace(namespace);
			cacheRefElement(context.evalNode("cache-ref"));
			cacheElement(context.evalNode("cache"));
			parameterMapElement(context.evalNodes("/mapper/parameterMap"));
			resultMapElements(context.evalNodes("/mapper/resultMap"));
			sqlElement(context.evalNodes("/mapper/sql"));
			buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
		} catch (Exception e) {
			throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e,e);
		}
	}

	private void buildStatementFromContext(List<XNode> list) {
		if (configuration.getDatabaseId() != null) {
			buildStatementFromContext(list, configuration.getDatabaseId());
		}
		buildStatementFromContext(list, null);
	}

	private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
		List<String> methods = getNamespaceRetuenPageMethods();
		for (XNode context : list) {
			context = buildSqlCountStatementContext(methods,context,requiredDatabaseId);
			final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant,context, requiredDatabaseId);
			try {
				statementParser.parseStatementNode();
			} catch (IncompleteElementException e) {
				configuration.addIncompleteStatement(statementParser);
			}
		}
	}

	private void parsePendingResultMaps() {
		Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
		synchronized (incompleteResultMaps) {
			Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().resolve();
					iter.remove();
				} catch (IncompleteElementException e) {
					// ResultMap is still missing a resource...
				}
			}
		}
	}

	private void parsePendingCacheRefs() {
		Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
		synchronized (incompleteCacheRefs) {
			Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().resolveCacheRef();
					iter.remove();
				} catch (IncompleteElementException e) {
					// Cache ref is still missing a resource...
				}
			}
		}
	}

	private void parsePendingStatements() {
		Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
		synchronized (incompleteStatements) {
			Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().parseStatementNode();
					iter.remove();
				} catch (IncompleteElementException e) {
					// Statement is still missing a resource...
				}
			}
		}
	}

	private void cacheRefElement(XNode context) {
		if (context != null) {
			configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
			CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant,context.getStringAttribute("namespace"));
			try {
				cacheRefResolver.resolveCacheRef();
			} catch (IncompleteElementException e) {
				configuration.addIncompleteCacheRef(cacheRefResolver);
			}
		}
	}

	private void cacheElement(XNode context) {
		if (context != null) {
			String type = context.getStringAttribute("type", "PERPETUAL");
			Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
			String eviction = context.getStringAttribute("eviction", "LRU");
			Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
			Long flushInterval = context.getLongAttribute("flushInterval");
			Integer size = context.getIntAttribute("size");
			boolean readWrite = !context.getBooleanAttribute("readOnly", false);
			boolean blocking = context.getBooleanAttribute("blocking", false);
			Properties props = context.getChildrenAsProperties();
			builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
		}
	}

	private void parameterMapElement(List<XNode> list) {
		for (XNode parameterMapNode : list) {
			String id = parameterMapNode.getStringAttribute("id");
			String type = parameterMapNode.getStringAttribute("type");
			Class<?> parameterClass = resolveClass(type);
			List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
			List<ParameterMapping> parameterMappings = new ArrayList<>();
			for (XNode parameterNode : parameterNodes) {
				String property = parameterNode.getStringAttribute("property");
				String javaType = parameterNode.getStringAttribute("javaType");
				String jdbcType = parameterNode.getStringAttribute("jdbcType");
				String resultMap = parameterNode.getStringAttribute("resultMap");
				String mode = parameterNode.getStringAttribute("mode");
				String typeHandler = parameterNode.getStringAttribute("typeHandler");
				Integer numericScale = parameterNode.getIntAttribute("numericScale");
				ParameterMode modeEnum = resolveParameterMode(mode);
				Class<?> javaTypeClass = resolveClass(javaType);
				JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
				Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
				ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property,javaTypeClass, jdbcTypeEnum, resultMap, modeEnum, typeHandlerClass, numericScale);
				parameterMappings.add(parameterMapping);
			}
			builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
		}
	}

	private void resultMapElements(List<XNode> list) throws Exception {
		for (XNode resultMapNode : list) {
			try {
				resultMapElement(resultMapNode);
			} catch (IncompleteElementException e) {
				// ignore, it will be retried
			}
		}
	}

	private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
		return resultMapElement(resultMapNode, Collections.<ResultMapping> emptyList(), null);
	}

	private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings,Class<?> enclosingType) throws Exception {
		ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
		String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
		String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType",resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
		String extend = resultMapNode.getStringAttribute("extends");
		Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
		Class<?> typeClass = resolveClass(type);
		if (typeClass == null) {
			typeClass = inheritEnclosingType(resultMapNode, enclosingType);
		}
		Discriminator discriminator = null;
		List<ResultMapping> resultMappings = new ArrayList<>();
		resultMappings.addAll(additionalResultMappings);
		List<XNode> resultChildren = resultMapNode.getChildren();
		for (XNode resultChild : resultChildren) {
			if ("constructor".equals(resultChild.getName())) {
				processConstructorElement(resultChild, typeClass, resultMappings);
			} else if ("discriminator".equals(resultChild.getName())) {
				discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
			} else {
				List<ResultFlag> flags = new ArrayList<>();
				if ("id".equals(resultChild.getName())) {
					flags.add(ResultFlag.ID);
				}
				resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
			}
		}
		ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend,discriminator, resultMappings, autoMapping);
		try {
			return resultMapResolver.resolve();
		} catch (IncompleteElementException e) {
			configuration.addIncompleteResultMap(resultMapResolver);
			throw e;
		}
	}

	protected Class<?> inheritEnclosingType(XNode resultMapNode, Class<?> enclosingType) {
		if ("association".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
			String property = resultMapNode.getStringAttribute("property");
			if (property != null && enclosingType != null) {
				MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
				return metaResultType.getSetterType(property);
			}
		} else if ("case".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
			return enclosingType;
		}
		return null;
	}

	private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings)throws Exception {
		List<XNode> argChildren = resultChild.getChildren();
		for (XNode argChild : argChildren) {
			List<ResultFlag> flags = new ArrayList<>();
			flags.add(ResultFlag.CONSTRUCTOR);
			if ("idArg".equals(argChild.getName())) {
				flags.add(ResultFlag.ID);
			}
			resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
		}
	}

	private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType,List<ResultMapping> resultMappings) throws Exception {
		String column = context.getStringAttribute("column");
		String javaType = context.getStringAttribute("javaType");
		String jdbcType = context.getStringAttribute("jdbcType");
		String typeHandler = context.getStringAttribute("typeHandler");
		Class<?> javaTypeClass = resolveClass(javaType);
		Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
		JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
		Map<String, String> discriminatorMap = new HashMap<>();
		for (XNode caseChild : context.getChildren()) {
			String value = caseChild.getStringAttribute("value");
			String resultMap = caseChild.getStringAttribute("resultMap",processNestedResultMappings(caseChild, resultMappings, resultType));
			discriminatorMap.put(value, resultMap);
		}
		return builderAssistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass,discriminatorMap);
	}

	private void sqlElement(List<XNode> list) {
		if (configuration.getDatabaseId() != null) {
			sqlElement(list, configuration.getDatabaseId());
		}
		sqlElement(list, null);
	}

	private void sqlElement(List<XNode> list, String requiredDatabaseId) {
		for (XNode context : list) {
			String databaseId = context.getStringAttribute("databaseId");
			String id = context.getStringAttribute("id");
			id = builderAssistant.applyCurrentNamespace(id, false);
			if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
				sqlFragments.put(id, context);
			}
		}
	}

	private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
		if (requiredDatabaseId != null) {
			if (!requiredDatabaseId.equals(databaseId)) {
				return false;
			}
		} else {
			if (databaseId != null) {
				return false;
			}
			// skip this fragment if there is a previous one with a not null
			// databaseId
			if (this.sqlFragments.containsKey(id)) {
				XNode context = this.sqlFragments.get(id);
				if (context.getStringAttribute("databaseId") != null) {
					return false;
				}
			}
		}
		return true;
	}

	private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags)throws Exception {
		String property;
		if (flags.contains(ResultFlag.CONSTRUCTOR)) {
			property = context.getStringAttribute("name");
		} else {
			property = context.getStringAttribute("property");
		}
		String column = context.getStringAttribute("column");
		String javaType = context.getStringAttribute("javaType");
		String jdbcType = context.getStringAttribute("jdbcType");
		String nestedSelect = context.getStringAttribute("select");
		String nestedResultMap = context.getStringAttribute("resultMap",processNestedResultMappings(context, Collections.<ResultMapping> emptyList(), resultType));
		String notNullColumn = context.getStringAttribute("notNullColumn");
		String columnPrefix = context.getStringAttribute("columnPrefix");
		String typeHandler = context.getStringAttribute("typeHandler");
		String resultSet = context.getStringAttribute("resultSet");
		String foreignColumn = context.getStringAttribute("foreignColumn");
		boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
		Class<?> javaTypeClass = resolveClass(javaType);
		Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
		JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
		return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum,
				                                   nestedSelect, nestedResultMap, notNullColumn, columnPrefix, 
				                                   typeHandlerClass, flags, resultSet,foreignColumn, lazy);
				
	}

	private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings,Class<?> enclosingType) throws Exception {
		if ("association".equals(context.getName()) || "collection".equals(context.getName())|| "case".equals(context.getName())) {
			if (context.getStringAttribute("select") == null) {
				validateCollection(context, enclosingType);
				ResultMap resultMap = resultMapElement(context, resultMappings, enclosingType);
				return resultMap.getId();
			}
		}
		return null;
	}

	protected void validateCollection(XNode context, Class<?> enclosingType) {
		if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null && context.getStringAttribute("resultType") == null) {
			MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
			String property = context.getStringAttribute("property");
			if (!metaResultType.hasSetter(property)) {
				throw new BuilderException("Ambiguous collection type for property '" + property+ "'. You must specify 'resultType' or 'resultMap'.");
			}
		}
	}

	private void bindMapperForNamespace() {
		String namespace = builderAssistant.getCurrentNamespace();
		if (namespace != null) {
			Class<?> boundType = null;
			try {
				boundType = Resources.classForName(namespace);
			} catch (ClassNotFoundException e) {
				// ignore, bound type is not required
			}
			if (boundType != null) {
				if (!configuration.hasMapper(boundType)) {
					// Spring may not know the real resource name so we set a
					// flag
					// to prevent loading again this resource from the mapper
					// interface
					// look at MapperAnnotationBuilder#loadXmlResource
					configuration.addLoadedResource("namespace:" + namespace);
					configuration.addMapper(boundType);
				}
			}
		}
	}
	
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    
	private XNode buildSqlCountStatementContext(List<String> methods,XNode context, String requiredDatabaseId) {
		String selectId = context.getStringAttribute("id");
		if(methods.contains(selectId)&&"select".equals(context.getName())){
			String contextXml = nodeToString(context.getNode());
			XNode sqlCountXNode = newSqlCountXNode(context,contextXml,selectId);
			final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant,sqlCountXNode, requiredDatabaseId);
			try {
				statementParser.parseStatementNode();
			} catch (IncompleteElementException e) {
				configuration.addIncompleteStatement(statementParser);
			}
			int lastIndexOf = contextXml.lastIndexOf("</select");
			if(lastIndexOf != -1){
				contextXml = new StringBuilder(contextXml.substring(0, lastIndexOf))
						         .append("<if test=\"")
						         .append(Pageable.PARAM_PROPERTY_SORT)
						         .append(" !=null\">")
						         .append("${")
						         .append(Pageable.PARAM_PROPERTY_SORT)
						         .append("}")
						         .append("</if>")
						         .append("\n</select>").toString();
			}
			contextXml = dialect.handlePageableSQL(contextXml, selectId);
			Node newContextNode = new XPathParser(contextXml).evalNode("select").getNode();
			context = context.newXNode(newContextNode);
			
		}
		return context;
	}
	
    private XNode newSqlCountXNode(XNode context,String contextXml,String selectId) {
    	String sqlCount = dialect.handleCountSQL(contextXml, selectId);
    	Node node = new XPathParser(sqlCount).evalNode("select").getNode();
    	return context.newXNode(node);
    }
    
    private List<String> getNamespaceRetuenPageMethods(){
		String namespace = builderAssistant.getCurrentNamespace();
		Class<?> boundType = null;
		if (namespace != null) {
			try {
				boundType = Resources.classForName(namespace);
			} catch (ClassNotFoundException e) {
			    throw new IllegalArgumentException("namespace：" + namespace+" class not found",e);
		    }
		}
		return Stream.of(boundType.getMethods()).filter(method->Page.class.isAssignableFrom(method.getReturnType())).map(Method::getName).collect(toList());
    }
    
    private String nodeToString(Node node) {
        try(StringWriter sw = new StringWriter();) {
    	    Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
    	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    	    transformer.transform(new DOMSource(node), new StreamResult(sw));
    	    return sw.toString();
    	} catch (IOException | TransformerException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @SuppressWarnings("unused")
	private Document newDocument(String str) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try(InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));) {
            return dbf.newDocumentBuilder().parse(is);
        } catch (SAXException | IOException | ParserConfigurationException e) {
        	throw new IllegalArgumentException(e);
        }
    }
    
}
