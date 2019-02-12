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
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SetSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.kabouros.mybatis.api.mapper.CrudMapper;
import com.kabouros.mybatis.core.mapping.EntityProperty;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;
/**
 * updateNoNullField sql
 * @author JIANG
 */
class HandleUpdateNoNullFieldStatement implements MappedStatementHandle {

	@Override
	public void handle(Configuration configuration, Class<?> mapperClass,MapperEntityMetadata<?> entityMetadata) {
		List<SqlNode> rootSqlNodes = new ArrayList<>();
		//update
		rootSqlNodes.add(new StaticTextSqlNode(String.join(" ", "update",entityMetadata.getTableName())));
		List<SqlNode> ifSqlNodes = new ArrayList<>();
		//set
		rootSqlNodes.add(new SetSqlNode(configuration,new MixedSqlNode(ifSqlNodes)));
		for(EntityProperty ep:entityMetadata.getEntityPropertys()) {
			if(!ep.isPrimarykey()){
				StringBuilder sb = new StringBuilder(ep.getColumnName()).append("=#{").append(ep.getName()).append("}").append(",");
				ifSqlNodes.add(new IfSqlNode(new StaticTextSqlNode(sb.toString()),String.join("!=",ep.getName(),"null")));
			}
		}
		StringBuilder where = new StringBuilder(" where 1 = 1 ");
		for(EntityProperty ep:entityMetadata.getEntityPropertys()){
			if(ep.isPrimarykey()){
				where.append("and ").append(ep.getColumnName()).append(" = ").append("#{").append(ep.getName()).append("} ");
			}
		}
		//where
		rootSqlNodes.add(new StaticTextSqlNode(where.toString()));
		String updateId = String.join(".",mapperClass.getName(),CrudMapper.METHOD_NAME_UPDATENONULLFIELD);
	    SqlSource sqlSource = new DynamicSqlSource(configuration,new MixedSqlNode(rootSqlNodes));
	    MappedStatement.Builder builder = new MappedStatement.Builder(configuration,updateId,sqlSource,SqlCommandType.UPDATE);
	    List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
	    builder.parameterMap( new ParameterMap.Builder(configuration,updateId + "-Inline",entityMetadata.getEntityType(),parameterMappings).build());
	    builder.flushCacheRequired(true);
	    MappedStatement ms = builder.resultMaps(Collections.emptyList()).build();
	    configuration.addMappedStatement(ms);
	}


}
