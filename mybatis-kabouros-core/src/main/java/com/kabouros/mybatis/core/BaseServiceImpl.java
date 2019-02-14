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
package com.kabouros.mybatis.core;

import java.io.Serializable;
import java.util.List;

import com.kabouros.mybatis.api.BaseService;
import com.kabouros.mybatis.api.domain.Page;
import com.kabouros.mybatis.api.domain.Pageable;
import com.kabouros.mybatis.api.mapper.BaseMapper;
import com.kabouros.mybatis.api.util.CollectionUtils;

/**
 * @author JIANG
 *
 * @param <T>
 * @param <ID>
 * @param <R>
 */
public abstract class BaseServiceImpl<T, ID extends Serializable, R extends BaseMapper<T, ID>> implements BaseService<T, ID> {
		
	public abstract R getCurrentMapper();
	
	@Override
	public int save(T t) {
		
		return getCurrentMapper().insert(t);
	}

	@Override
	public int saveAll(List<T> list) {
		if(!CollectionUtils.isEmpty(list)){
			return getCurrentMapper().insertAll(list);
		}
		return 0;
	}
	
	@Override
	public int update(T t){
		
		return getCurrentMapper().update(t);
	}
	
	@Override
	public int updateNotNullProperty(T t){
		
		return getCurrentMapper().updateNotNullProperty(t);
	}
	
	@Override
	public T queryByPrimaryKey(ID id){
		
		return getCurrentMapper().selectByPrimaryKey(id);
	}
	
	@Override
	public int deleteByPrimaryKey(ID id){
		
		return getCurrentMapper().deleteByPrimaryKey(id);
	}
	
	@Override
	public int deleteByPrimaryKeys(List<ID> ids){
		if(!CollectionUtils.isEmpty(ids)){
			return getCurrentMapper().deleteByPrimaryKeys(ids);
		}
		return 0;
	}
	
	@Override
	public T queryFieldByPrimaryKey(String fieldStr,ID id){
		if(null == fieldStr){
			fieldStr = "*";
		}
		return getCurrentMapper().selectFieldByPrimaryKey(fieldStr, id);
	}
	
	@Override
	public List<T> queryAll(){
		
		return getCurrentMapper().selectAll();
	}
	
	@Override
	public Page<T> queryAllByPageable(Pageable pageable){
		
		return getCurrentMapper().selectAllByPageable(pageable);
	}
	
	@Override
	public long queryTotalCount(){
		
		return getCurrentMapper().selectTotalCount();
	}

}
