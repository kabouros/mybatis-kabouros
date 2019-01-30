/**
 * MIT License
 * Copyright (c) 2018 jiangcihuo
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

import com.kabouros.mybatis.api.domain.Pageable;

public class MySQLDialect implements Dialect {

	@Override
	public String processSQLPageable(String contextXml, String selectId) {
		int lastIndexOf = contextXml.lastIndexOf("</select");
		if(lastIndexOf != -1){
			contextXml = new StringBuilder(contextXml.substring(0, lastIndexOf)).append(Pageable.LIMIT_SQL).append("\n</select>").toString();
		}
		return contextXml;
	}

	@Override
	public String processSQLCount(String contextXml, String selectId) {
    	String body = REGEX_SELECT_LABEL.matcher(contextXml).replaceFirst("");
    	if(null != body){
    		int fromIndex = body.indexOf("from");
    		if(fromIndex != -1){
    			body = String.join(" ", "select count(0)",body.substring(fromIndex));
    		}
    	}
    	String str = new StringBuilder("<select").append(" id=\"").append(selectId).append("-count\"").append(" resultType=\"long\">\n ").append(body).toString();
		return str;
	}

}
