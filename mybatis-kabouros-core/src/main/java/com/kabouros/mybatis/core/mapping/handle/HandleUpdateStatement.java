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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.kabouros.mybatis.api.mapper.CrudMapper;
import com.kabouros.mybatis.core.dialect.Dialect;
import com.kabouros.mybatis.core.mapping.EntityProperty;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;
/**
 * updateAllField sql
 * @author JIANG
 */
public class HandleUpdateStatement extends AbstractMappedStatementHandle implements MappedStatementHandle{

	public HandleUpdateStatement(Dialect dialect) {
		super(dialect);
	}

	@Override
	public void handle(Configuration configuration, Class<?> mapperClass,MapperEntityMetadata<?> entityMetadata) {
		String updateId = String.join(".",mapperClass.getName(),CrudMapper.METHOD_NAME_UPDATE);
		if(!configuration.hasStatement(updateId)) {
			StringBuilder sb = new StringBuilder("update ").append(entityMetadata.getTableName()).append(" set ");
			for(EntityProperty ep:entityMetadata.getEntityPropertys()) {
				if(!ep.isPrimarykey()){
					sb.append(ep.getColumnName()).append("=").append("#{").append(ep.getName()).append("},");
				}
			}
			sb.deleteCharAt(sb.length() - 1).append(" where 1 = 1 ");
			for(EntityProperty ep:entityMetadata.getEntityPropertys()){
				if(ep.isPrimarykey()){
					sb.append("and ").append(ep.getColumnName()).append(" = ").append("#{").append(ep.getName()).append("} ");
				}
			}
			addMappedStatement(configuration,sb.toString(),entityMetadata.getEntityType(),updateId,SqlCommandType.UPDATE,null);
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
