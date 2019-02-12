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
package com.kabouros.mybatis.core.mapping.handle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.kabouros.mybatis.api.mapper.CrudMapper;
import com.kabouros.mybatis.core.mapping.EntityProperty;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;
/**
 * insertAll sql
 * @author JIANG
 */
class HandleInsertAllStatement implements MappedStatementHandle{

	@Override
	public void handle(Configuration configuration, Class<?> mapperClass,MapperEntityMetadata<?> entityMetadata) {
		String insertId = String.join(".",mapperClass.getName(),CrudMapper.METHOD_NAME_INSERTALL);
		if(!configuration.hasStatement(insertId)) {
			List<SqlNode> rootSqlNodes = new ArrayList<>();
			StringBuilder insert = new StringBuilder("insert into ").append(entityMetadata.getTableName()).append(" (");
			for(EntityProperty ep:entityMetadata.getEntityPropertys()){
				insert.append(ep.getColumnName()).append(",");
			}
			insert.deleteCharAt(insert.length() - 1).append(") values ");
			//insert
			rootSqlNodes.add(new StaticTextSqlNode(insert.toString()));
			//values
			String itemName = "item";
			StringBuilder values = new StringBuilder("(");
			for(EntityProperty ep:entityMetadata.getEntityPropertys()){
				values.append("#{").append(itemName).append(".").append(ep.getName()).append("},");
			}
			values.deleteCharAt(values.length() - 1).append(")");
			ForEachSqlNode forEachSqlNode = new ForEachSqlNode(configuration,new StaticTextSqlNode(values.toString()),"list",null,itemName,null,null,",");
			rootSqlNodes.add(forEachSqlNode);
			SqlSource sqlSource = new DynamicSqlSource(configuration,new MixedSqlNode(rootSqlNodes));
		    MappedStatement.Builder builder = new MappedStatement.Builder(configuration,insertId,sqlSource,SqlCommandType.INSERT);
		    List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
		    builder.parameterMap( new ParameterMap.Builder(configuration,insertId + "-Inline",List.class,parameterMappings).build());
		    builder.flushCacheRequired(true);
		    MappedStatement ms = builder.resultMaps(Collections.emptyList()).build();
		    configuration.addMappedStatement(ms);
		}
	}

}
