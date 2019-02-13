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
import java.util.List;

import com.kabouros.mybatis.api.domain.Page;
import com.kabouros.mybatis.api.domain.Pageable;

/**
 * @author JIANG
 *
 * @param <T> entity
 */
public interface CrudMapper<T,ID extends Serializable> {
	
	String METHOD_NAME_INSERT = "insert";
	String METHOD_NAME_INSERTALL = "insertAll";
	String METHOD_NAME_UPDATE = "update";
	String METHOD_NAME_UPDATENOTNULLPROPERTY = "updateNotNullProperty";
	String METHOD_NAME_SELECTBYPRIMARYKEY = "selectByPrimaryKey";
	String METHOD_NAME_DELETEBYPRIMARYKEY = "deleteByPrimaryKey";
	String METHOD_NAME_DELETEBYPRIMARYKEYS = "deleteByPrimaryKeys";
	String METHOD_NAME_SELECTFIELDBYPRIMARYKEY = "selectFieldByPrimaryKey";
	String METHOD_NAME_SELECTALL = "selectAll";
	String METHOD_NAME_SELECTALLBYPAGEABLE = "selectAllByPageable";
	String METHOD_NAME_COUNT = "selectTotalCount";
	
	/**
	 * Insert single entity
	 * @param t entity
	 * @return number of affected rows
	 */
	int insert(T t);
	/**
	 * Batch insert entity
	 * @param list entitys
	 * @return number of affected rows
	 */
	int insertAll(List<T> list);
	/**
	 * Update entity not null property
	 * @param t entity
	 * @return number of affected rows
	 */
	int updateNotNullProperty(T t);
	/**
	 * Update entity all field
	 * @param t entity
	 * @return number of affected rows
	 */
	int update(T t);
	/**
	 * Query by primary key
	 * @param id primary key
	 * @return entity
	 */
	T selectByPrimaryKey(ID id);
	
	/**
	 * Delete by primary key
	 * @param id primary key
	 * @return number of affected rows
	 */
	int deleteByPrimaryKey(ID id);
	
	/**
	 * Delete by primary keys
	 * @param ids id primary keys
	 * @return number of affected rows
	 */
	int deleteByPrimaryKeys(List<ID> ids);
	
	/**
	 * Select field by primary key
	 * @param fieldStr attributes are separated by commas
	 * @param id primary key
	 * @return entity
	 */
	T selectFieldByPrimaryKey(String fieldStr,ID id);
	
	/**
	 * Select all
	 * @return entity
	 */
	List<T> selectAll();
	
	/**
	 * Select all by pageable
	 * @param pageable page entity
	 * @return page
	 */
	Page<T> selectAllByPageable(Pageable pageable);
	
	/**
	 * Returns the number of entities available.
	 * @return the number of entities
	 */
	long selectTotalCount();
	
}
