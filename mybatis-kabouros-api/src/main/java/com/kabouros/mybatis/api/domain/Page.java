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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.kabouros.mybatis.api.Converter;
import com.kabouros.mybatis.api.util.Assert;

/**
 * @author JIANG
 *
 * @param <T> data type
 */
public class Page<T> implements Iterable<T>,Serializable{
	
	private static final long serialVersionUID = 4831811669388154375L;
	
	private final List<T> content = new ArrayList<T>();
	private final Pageable pageable;
	private final long totalCount;
	private final long totalPage;
	
	public Page(List<T> content) {
		
		this(content, null, null == content ? 0 : content.size());
	}

	public Page(List<T> content, Pageable pageable,long total) {
		Assert.notEmpty(content, "Content must not be null!");
		this.content.addAll(content);
		this.pageable = pageable;
		this.totalCount = total;
		this.totalPage = getPageSize() == 0 ? 1 : (long) Math.ceil((double) totalCount / (double) getPageSize());
	}
	
	public long getTotalPage() {
		
		return totalPage;
	}
	
	public long getTotalCount() {
		
		return totalCount;
	}
	
	public int getPageSize() {
		
		return pageable == null ? 0 : pageable.getPageSize();
	}
	
	public int getPageNumber() {
		
		return pageable == null ? 0 : pageable.getPageNumber();
	}
	
	public boolean hasNext() {
		
		return getPageNumber() + 1 < getTotalPage();
	}
	
	public int getNumberOfElements() {
		
		return content.size();
	}
	
	public boolean hasPrevious() {
		
		return getPageNumber() > 0;
	}
	
	public Pageable nextPageable() {
		return hasNext() ? pageable.next() : null;
	}

	public Pageable previousPageable() {
		if (hasPrevious()) {
			return pageable.previousOrFirst();
		}
		return null;
	}
	
	public List<T> getContent() {
		
		return Collections.unmodifiableList(content);
	}
	
	public Sort getSort() {
		
		return pageable == null ? null : pageable.getSort();
	}

	@Override
	public Iterator<T> iterator() {
		
		return content.iterator();
	}
	
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		Assert.notNull(converter, "Converter must not be null!");
		List<S> result = new ArrayList<S>(content.size());
		for (T element : this) {
			result.add(converter.convert(element));
		}
		return new Page<S>(result, pageable, totalCount);
	}

	@Override
	public String toString() {
		return "Page [content=" + content + ", pageable=" + pageable + ", totalCount=" + totalCount + ", totalPage=" + totalPage +"]";
	}
	
}
