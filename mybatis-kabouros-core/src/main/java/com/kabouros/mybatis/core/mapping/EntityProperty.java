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
package com.kabouros.mybatis.core.mapping;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.kabouros.mybatis.core.util.ReflectionUtil;

/**
 * @author JIANG
 */
public class EntityProperty extends PropertyDescriptor {
	
	
	private Class<?> javaType;
	// column name
	private String columnName;
	// is primary key
	private boolean primarykey;
	
	private boolean transienta;
	
	public EntityProperty(String propertyName, Class<?> beanClass) throws IntrospectionException {
		super(propertyName, beanClass);
	}

	public String getColumnName() {

		return columnName;
	}

	public void setColumnName(String columnName) {

		this.columnName = columnName;
	}

	public boolean isPrimarykey() {

		return primarykey;
	}

	public void setPrimarykey(boolean primarykey) {

		this.primarykey = primarykey;
	}
	
	public Class<?> getJavaType() {
		return javaType;
	}

	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public boolean isTransienta() {
		return transienta;
	}

	public void setTransienta(boolean transienta) {
		this.transienta = transienta;
	}

	public Object getPropertyValue(Object object) {
		
		return ReflectionUtil.invokeMethod(this.getReadMethod(), object);
	}

	public void setPropertyValue(Object object,Object value) {
		
		ReflectionUtil.invokeMethod(this.getWriteMethod(), object,value);
	}
	
}
