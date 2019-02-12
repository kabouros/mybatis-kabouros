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
package com.kabouros.mybatis.core.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import com.kabouros.mybatis.core.binding.MapperRegistry;


/**
 * 重写 MapperRegistry 相关
 * @author JIANG
 */
public class SessionConfiguration extends Configuration {

	protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

	@Override
	public MapperRegistry getMapperRegistry() {

		return mapperRegistry;
	}
    
	@Override
	public void addMappers(String packageName, Class<?> superType) {

		mapperRegistry.addMappers(packageName, superType);
	}

	@Override
	public void addMappers(String packageName) {

		mapperRegistry.addMappers(packageName);
	}

	@Override
	public <T> void addMapper(Class<T> type) {

		mapperRegistry.addMapper(type);
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {

		return mapperRegistry.getMapper(type, sqlSession);
	}

	@Override
	public boolean hasMapper(Class<?> type) {

		return mapperRegistry.hasMapper(type);
	}

}
