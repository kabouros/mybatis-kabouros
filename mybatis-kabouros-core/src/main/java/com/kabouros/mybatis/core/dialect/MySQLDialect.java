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
package com.kabouros.mybatis.core.dialect;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.kabouros.mybatis.api.domain.Pageable;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;

public class MySQLDialect implements Dialect {

	@Override
	public String handlePageableSQL(String contextXml, String selectId) {
		int lastIndexOf = contextXml.lastIndexOf("</select");
		if(lastIndexOf != -1){
			contextXml = new StringBuilder(contextXml.substring(0, lastIndexOf)).append(Pageable.LIMIT_SQL).append("\n</select>").toString();
		}
		return contextXml;
	}

	@Override
	public String handleCountSQL(String contextXml, String selectId) {
    	String body = REGEX_SELECT_LABEL.matcher(contextXml).replaceFirst("");
    	if(null != body){
    		int fromIndex = body.indexOf("from");
    		if(fromIndex != -1){
    			body = String.join(" ", "select count(0)",body.substring(fromIndex));
    		}
    	}
    	String str = new StringBuilder("<select").append(" id=\"").append(selectId).append(Pageable.SQL_COUNT_SUFFIX).append("\" resultType=\"long\">\n ").append(body).toString();
		return str;
	}
	
	@Override
	public SqlSource createSelectByPageableSqlSource(Configuration configuration,String selectId,MapperEntityMetadata<?> entityMetadata){
		List<SqlNode> rootSqlNodes = new ArrayList<>();
		rootSqlNodes.add(new StaticTextSqlNode(new StringBuilder("select * from ").append(entityMetadata.getTableName()).append(" where 1 = 1 ").toString()));
		//sort
		rootSqlNodes.add(new IfSqlNode(new TextSqlNode(String.join("","${",Pageable.PARAM_PROPERTY_SORT,"}")),String.join("!=",Pageable.PARAM_PROPERTY_SORT,"null")));
		//limit
		rootSqlNodes.add(new StaticTextSqlNode(Pageable.LIMIT_SQL));
	    MixedSqlNode sqlNode = new MixedSqlNode(rootSqlNodes);
	    return new DynamicSqlSource(configuration,sqlNode);
	}

}
