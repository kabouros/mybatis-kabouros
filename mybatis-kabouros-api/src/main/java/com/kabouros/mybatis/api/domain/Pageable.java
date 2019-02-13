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
package com.kabouros.mybatis.api.domain;

/**
 * @author JIANG
 */
public interface Pageable {
	
	String PARAM_NAME_START = "start";
	String PARAM_NAME_OFFSET = "offset";
	String PARAM_NAME_SORT = "sort";
	String SQL_COUNT_SUFFIX = "-count";
	String NAME = Pageable.class.getSimpleName().toLowerCase();
	String PARAM_PROPERTY_START = String.join(".", NAME,PARAM_NAME_START);
	String PARAM_PROPERTY_OFFSET = String.join(".", NAME,PARAM_NAME_OFFSET);
	String PARAM_PROPERTY_SORT = String.join(".", NAME,PARAM_NAME_SORT);
	String LIMIT_SQL = String.join("", " limit #{",PARAM_PROPERTY_START,"},","#{",PARAM_PROPERTY_OFFSET,"} ");
	
	int getPageNumber();

	int getPageSize();

	int getStart();

	int getOffset();

	Sort getSort();

	Pageable previous();

	Pageable next();

	Pageable previousOrFirst();

	Pageable first();

	boolean hasPrevious();

	boolean isErrorPageNumber(double totalCounts);

}
