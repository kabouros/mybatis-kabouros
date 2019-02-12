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
package com.kabouros.mybatis.core.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;

import com.kabouros.mybatis.api.exception.AnnotationMapperException;
import com.kabouros.mybatis.api.mapper.CrudMapper;
import com.kabouros.mybatis.core.mapping.DefaultMapperEntityMetadata;
import com.kabouros.mybatis.core.mapping.EntityProperty;
import com.kabouros.mybatis.core.mapping.MapperEntityMetadata;
import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandle;
import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandleRegistry;
import com.kabouros.mybatis.core.util.ClassUtil;

/**
 * 接口映射代理工厂
 * 
 * @see org.apache.ibatis.binding.MapperProxyFactory
 * 
 * @author JIANG
 */
public class MapperProxyFactory<T> {

	private final Class<T> mapperInterface;
	private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();
	private final MapperEntityMetadata<?> mapperEntityMetadata;

    public MapperProxyFactory(Class<T> mapperInterface) {
	    this.mapperInterface = mapperInterface;
	    this.mapperEntityMetadata = new DefaultMapperEntityMetadata<>(mapperInterface);
	}

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, MapperMethod> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
    }
    
    public T newInstance(SqlSession sqlSession) {
    	mappedStatementHandleRegistry(sqlSession);
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }
    
    private void mappedStatementHandleRegistry(SqlSession sqlSession){
		if(CrudMapper.class.isAssignableFrom(mapperInterface)){
			int primaryKeyCount = 0;
			for(EntityProperty ep:mapperEntityMetadata.getEntityPropertys()) {
				if(ep.isPrimarykey()) {
					primaryKeyCount++;
				}
			}
			if(primaryKeyCount == 0) {
				throw new AnnotationMapperException(mapperEntityMetadata.getEntityType().getName()+" no identifier<@Id> specified");
			}
			if(primaryKeyCount > 1 && ClassUtil.isBaseType(mapperEntityMetadata.getPrimaryKeyType())) {
				throw new AnnotationMapperException(mapperInterface.getName()+" error superclass identifier generic, composite primary key suggested use "+mapperEntityMetadata.getEntityType().getSimpleName());
			}
			if(primaryKeyCount == 1 && !ClassUtil.isBaseType(mapperEntityMetadata.getPrimaryKeyType())) {
				throw new AnnotationMapperException(mapperInterface.getName()+" error superclass identifier generic");
			}
			List<MappedStatementHandle> mappedStatementHandles = MappedStatementHandleRegistry.getInstance().getRegistry();
			for(MappedStatementHandle handle:mappedStatementHandles) {
				handle.handle(sqlSession.getConfiguration(), mapperInterface, mapperEntityMetadata);
			}
		}
    }
}
