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
package com.kabouros.mybatis.api.domain;

import com.kabouros.mybatis.api.domain.Sort.Direction;
/**
 * @author JIANG
 */
public class PageRequest implements Pageable {
	
	private int pageNumber;
	
	private int pageSize;
	
	private Sort sort;
	
	public static PageRequest of(int pageNumber, int pageSize) {
		
		return new PageRequest(pageNumber,pageSize,null);
	}
	
	public static PageRequest of(int pageNumber, int pageSize, Direction direction, String... properties) {
		
		return new PageRequest(pageNumber, pageSize, new Sort(direction, properties));
	}
	
	public static PageRequest of(int pageNumber, int pageSize, Sort sort){
		
		return new PageRequest(pageNumber,pageSize,sort);
	}
	
	private PageRequest(){
		
	}
	
	private PageRequest(int pageNumber, int pageSize, Sort sort) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.sort = sort;
	}
	
	@Override
	public int getPageNumber() {
		
		return pageNumber < 1 ? 1 : pageNumber;
	}

	@Override
	public int getPageSize() {
		
		return pageSize;
	}
	
	@Override
	public int getStart(){
		
		return (getPageNumber() - 1) * pageSize;
	}

	@Override
	public int getOffset() {
		
		return getPageNumber() * pageSize;
	}

	@Override
	public Sort getSort() {
		return sort;
	}
	
	@Override
	public PageRequest previous() {
		
		return getPageNumber() == 0 ? this : new PageRequest(getPageNumber() - 1, getPageSize(), getSort());
	}

	@Override
	public PageRequest next(){
		
		return new PageRequest(getPageNumber() + 1, getPageSize(), getSort());
	}

	@Override
	public PageRequest previousOrFirst() {
		
		return hasPrevious() ? previous() : first();
	}

	@Override
	public PageRequest first() {
		
		return new PageRequest(0, getPageSize(), getSort());
	}

	@Override
	public boolean hasPrevious() {
		
		return pageNumber > 0;
	}
	
	@Override
	public boolean isErrorPageNumber(double totalCounts){
		
		return pageSize == 0 || getPageNumber() > (long) Math.ceil(totalCounts / (double) pageSize);
	}

	@Override
	public String toString() {
		return "PageRequest [pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", sort=" + sort + "]";
	}
	
}
