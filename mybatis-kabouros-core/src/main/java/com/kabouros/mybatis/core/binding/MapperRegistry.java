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
package com.kabouros.mybatis.core.binding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * 重写 getMapper，hasMapper，addMapper，getMappers 函数，以达到替换原有的 MapperProxyFactory 目的
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Lasse Voss
 * @author JIANG
 */
public class MapperRegistry extends org.apache.ibatis.binding.MapperRegistry {

	private final Configuration config;
	private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

	public MapperRegistry(Configuration config) {
		super(config);
		this.config = config;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
	    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
	    if (mapperProxyFactory == null) {
	        throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
	    }
	    try {
	        return mapperProxyFactory.newInstance(sqlSession);
	    } catch (Exception e) {
	        throw new BindingException("Error getting mapper instance. Cause: " + e, e);
	    }
	}
   
	@Override
	public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

	@Override
	public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
		    if (hasMapper(type)) {
		        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
		    }
		    boolean loadCompleted = false;
		    try {
		        knownMappers.put(type, new MapperProxyFactory<>(type));
		        // It's important that the type is added before the parser is run
		        // otherwise the binding may automatically be attempted by the
		        // mapper parser. If the type is already known, it won't try.
		        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
		        parser.parse();
		        loadCompleted = true;
		    } finally {
		        if (!loadCompleted) {
		            knownMappers.remove(type);
		        }
		    }
		}
	}

	@Override
	public Collection<Class<?>> getMappers() {
        return Collections.unmodifiableCollection(knownMappers.keySet());
    }

}
