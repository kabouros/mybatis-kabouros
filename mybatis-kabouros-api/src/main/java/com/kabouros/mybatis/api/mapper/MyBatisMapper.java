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
package com.kabouros.mybatis.api.mapper;

import java.io.Serializable;

import com.kabouros.mybatis.api.domain.Page;
import com.kabouros.mybatis.api.domain.Pageable;

/**
 * @author JIANG
 *
 * @param <T> Entity
 * @param <ID> Primary Key
 */
public interface MyBatisMapper<T,ID extends Serializable> extends CrudMapper<T,ID> {
	
	String METHOD_NAME_SELECTALLBYPAGEABLE = "selectAllByPageable";
	
	
	/**
	 * Select all by pageable
	 * @param pageable page entity
	 * @return page
	 */
	Page<T> selectAllByPageable(Pageable pageable);
	
}
