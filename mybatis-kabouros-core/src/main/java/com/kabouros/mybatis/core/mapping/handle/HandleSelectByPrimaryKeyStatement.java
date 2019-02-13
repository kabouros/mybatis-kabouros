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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.kabouros.mybatis.api.mapper.CrudMapper;
import com.kabouros.mybatis.core.mapping.EntityProperty;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;

/**
 * selectByPrimaryKey sql
 * @author JIANG
 */
public class HandleSelectByPrimaryKeyStatement implements MappedStatementHandle {

	@Override
	public void handle(Configuration configuration, Class<?> mapperClass,MapperEntityMetadata<?> entityMetadata) {
		String selectId = String.join(".",mapperClass.getName(),CrudMapper.METHOD_NAME_SELECTBYPRIMARYKEY);
		if(!configuration.hasStatement(selectId)) {
			StringBuilder sb = new StringBuilder("select * from ").append(entityMetadata.getTableName()).append(" where 1 = 1 ");
			for(EntityProperty ep:entityMetadata.getEntityPropertys()){
				if(ep.isPrimarykey()){
					sb.append("and ").append(ep.getColumnName()).append(" = ").append("#{").append(ep.getName()).append("} ");
				}
			}
			ResultMap resultMap = new ResultMap.Builder(configuration,selectId + "-Inline",entityMetadata.getEntityType(), new ArrayList<ResultMapping>(),null).build();
		    addMappedStatement(configuration,sb.toString(),entityMetadata.getPrimaryKeyType(),selectId,SqlCommandType.SELECT,Arrays.asList(resultMap));
		}
	}
	
	/**
	 * @param sql
	 * @param parameterType
	 * @param id
	 * @param sqlCommandType
	 * @param resultMaps
	 */
	protected void addMappedStatement(Configuration configuration, String sql,Class<?> parameterType,String id,SqlCommandType sqlCommandType,List<ResultMap> resultMaps){
	    MixedSqlNode sqlNode = new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(sql)));
	    SqlSource sqlSource = new RawSqlSource(configuration,sqlNode,parameterType);
	    MappedStatement.Builder builder = new MappedStatement.Builder(configuration,id,sqlSource,sqlCommandType);
	    if(null == resultMaps){
	    	resultMaps = Collections.emptyList();
	    }
	    MappedStatement ms = builder.resultMaps(resultMaps).build();
	    configuration.addMappedStatement(ms);
	}


}
