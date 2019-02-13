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

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.kabouros.mybatis.api.domain.Pageable;
import com.kabouros.mybatis.api.mapper.CrudMapper;
import com.kabouros.mybatis.core.dialect.Dialect;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;

/**
 * selectAllByPageable sql
 * @author JIANG
 */
public class HandleSelectAllByPageableStatement extends AbstractMappedStatementHandle implements MappedStatementHandle {
	
	public HandleSelectAllByPageableStatement(Dialect dialect) {
		super(dialect);
	}

	
	/*@Override
	public void handle(Configuration configuration, Class<?> mapperClass,MapperEntityMetadata<?> entityMetadata) {
		String selectId = String.join(".",mapperClass.getName(),CrudMapper.METHOD_NAME_SELECTALLBYPAGEABLE);
		if(!configuration.hasStatement(selectId)) {
			List<SqlNode> rootSqlNodes = new ArrayList<>();
			rootSqlNodes.add(new StaticTextSqlNode(new StringBuilder("select * from ").append(entityMetadata.getTableName()).append(" where 1 = 1 ").toString()));
			//sort
			rootSqlNodes.add(new IfSqlNode(new TextSqlNode(String.join("","${",Pageable.PARAM_PROPERTY_SORT,"}")),String.join("!=",Pageable.PARAM_PROPERTY_SORT,"null")));
			//limit
			rootSqlNodes.add(new StaticTextSqlNode(Pageable.LIMIT_SQL));
			ResultMap resultMap = new ResultMap.Builder(configuration,selectId + "-Inline",entityMetadata.getEntityType(), new ArrayList<ResultMapping>(),null).build();
		    MixedSqlNode sqlNode = new MixedSqlNode(rootSqlNodes);
		    SqlSource sqlSource = new DynamicSqlSource(configuration,sqlNode);
		    MappedStatement.Builder builder = new MappedStatement.Builder(configuration,selectId,sqlSource,SqlCommandType.SELECT);
		    MappedStatement ms = builder.resultMaps(Arrays.asList(resultMap)).build();
		    configuration.addMappedStatement(ms);
		    //count sql
			StringBuilder countSql = new StringBuilder("select count(0) from ").append(entityMetadata.getTableName());
			ResultMap countResultMap = new ResultMap.Builder(configuration,selectId + "-Inline",long.class, new ArrayList<ResultMapping>(),null).build();
		    SqlSource countSqlSource = new RawSqlSource(configuration,new StaticTextSqlNode(countSql.toString()),null);
		    MappedStatement.Builder countBuilder = new MappedStatement.Builder(configuration,String.join("",selectId,Pageable.SQL_COUNT_SUFFIX),countSqlSource,SqlCommandType.SELECT);
		    MappedStatement countMs = countBuilder.resultMaps(Arrays.asList(countResultMap)).build();
		    configuration.addMappedStatement(countMs);
		}
	}*/

	@Override
	public void handle(Configuration configuration, Class<?> mapperClass,MapperEntityMetadata<?> entityMetadata) {
		String selectId = String.join(".",mapperClass.getName(),CrudMapper.METHOD_NAME_SELECTALLBYPAGEABLE);
		if(!configuration.hasStatement(selectId)) {
			SqlSource sqlSource = dialect.createSelectByPageableSqlSource(configuration, selectId, entityMetadata);
			ResultMap resultMap = new ResultMap.Builder(configuration,selectId + "-Inline",entityMetadata.getEntityType(), new ArrayList<ResultMapping>(),null).build();
		    MappedStatement.Builder builder = new MappedStatement.Builder(configuration,selectId,sqlSource,SqlCommandType.SELECT);
		    MappedStatement ms = builder.resultMaps(Arrays.asList(resultMap)).build();
		    configuration.addMappedStatement(ms);
		    //count sql
			StringBuilder countSql = new StringBuilder("select count(0) from ").append(entityMetadata.getTableName());
			ResultMap countResultMap = new ResultMap.Builder(configuration,selectId + "-Inline",long.class, new ArrayList<ResultMapping>(),null).build();
		    SqlSource countSqlSource = new RawSqlSource(configuration,new StaticTextSqlNode(countSql.toString()),null);
		    MappedStatement.Builder countBuilder = new MappedStatement.Builder(configuration,String.join("",selectId,Pageable.SQL_COUNT_SUFFIX),countSqlSource,SqlCommandType.SELECT);
		    MappedStatement countMs = countBuilder.resultMaps(Arrays.asList(countResultMap)).build();
		    configuration.addMappedStatement(countMs);
		}
	}
	
}
