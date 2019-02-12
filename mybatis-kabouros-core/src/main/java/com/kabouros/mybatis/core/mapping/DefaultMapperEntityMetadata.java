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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kabouros.mybatis.api.annotation.Id;
import com.kabouros.mybatis.api.annotation.Table;
import com.kabouros.mybatis.api.annotation.Transient;
import com.kabouros.mybatis.api.util.Assert;
import com.kabouros.mybatis.core.util.ClassUtil;
import com.kabouros.mybatis.core.util.ReflectionUtil;

/**
 * @author JIANG
 * @param <T> entity
 */
public class DefaultMapperEntityMetadata<T> implements MapperEntityMetadata<T> {

	private final String tableName;
	private final Class<?> mapperType;
	private final Class<?> entityType;
	private final Class<?> primaryKeyType;
	
	private List<EntityProperty> entityPropertys;
	
	private final NamingStrategy namingStrategy = new UnderscoresNamingStrategy();

	public DefaultMapperEntityMetadata(Class<?> mapperClazz) {
	    this.entityType = ClassUtil.getInterfaceGeneric(mapperClazz,0);
		Assert.notNull(entityType,String.format("%s superclass does not have entity generic", mapperClazz));
		this.primaryKeyType = ClassUtil.getInterfaceGeneric(mapperClazz,1);
		Assert.notNull(primaryKeyType,String.format("%s superclass does not have identifier generic", mapperClazz));
		this.mapperType = mapperClazz;
		Table table = entityType.getAnnotation(Table.class);
		if(null != table) {
			String tableName = table.name();
			Assert.hasLength(tableName,String.format("%s table name() is null or empty", entityType));
			this.tableName = tableName;
		} else {
			this.tableName = namingStrategy.propertyToColumnName(entityType.getName());
		}
	}
	
	@Override
	public List<EntityProperty> getEntityPropertys() {
		if(this.entityPropertys == null){
			this.entityPropertys = new ArrayList<>();
			ReflectionUtil.doWithFields(entityType, field-> {
			    if(!Modifier.isStatic(field.getModifiers())){
				    String fieldName = field.getName();
					try {
						EntityProperty ep = new EntityProperty(fieldName,entityType);
						ep.setJavaType(field.getType());
						ep.setColumnName(namingStrategy.propertyToColumnName(fieldName));
						if(null != field.getAnnotation(Id.class)){
							ep.setPrimarykey(true);
						}
						if(null != field.getAnnotation(Transient.class)){
							ep.setTransienta(true);
						}
						entityPropertys.add(ep);
					} catch (Exception e) {
						throw new IllegalArgumentException(e);
					}
				}
			});
		}
		return Collections.unmodifiableList(entityPropertys);
	}
	
	@Override
	public Class<?> getEntityType() {
		
		return entityType;
	}

	@Override
	public String getTableName() {
		
		return tableName;
	}
	
	public Class<?> getMapperType(){
		
		return mapperType;
	}

	@Override
	public Class<?> getPrimaryKeyType() {
		
		return primaryKeyType;
	}
	
}
