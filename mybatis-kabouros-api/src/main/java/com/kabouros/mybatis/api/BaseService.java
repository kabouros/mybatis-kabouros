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
package com.kabouros.mybatis.api;

import java.util.List;

import com.kabouros.mybatis.api.domain.Page;
import com.kabouros.mybatis.api.domain.Pageable;

/**
 * @author JIANG
 *
 * @param <T>
 * @param <ID>
 */
public interface BaseService <T,ID> {
	
	int save(T t);
	
	int saveAll(List<T> list);
	
	int update(T t);
	
	int updateNotNullProperty(T t);
	
	T queryByPrimaryKey(ID id);
	
	int deleteByPrimaryKey(ID id);
	
	int deleteByPrimaryKeys(List<ID> ids);
	
	T queryFieldByPrimaryKey(String fieldStr,ID id);
	
	List<T> queryAll();
	
	Page<T> queryAllByPageable(Pageable pageable);
	
	long queryTotalCount();

}
