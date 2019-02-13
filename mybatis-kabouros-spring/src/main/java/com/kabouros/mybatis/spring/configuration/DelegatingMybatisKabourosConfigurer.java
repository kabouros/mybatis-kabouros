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

package com.kabouros.mybatis.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.kabouros.mybatis.core.dialect.Dialect;
import com.kabouros.mybatis.core.mapping.handle.HandleDeleteByPrimaryKeyStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleDeleteByPrimaryKeysStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleInsertAllStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleInsertStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleSelectAllByPageableStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleSelectAllStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleSelectByPrimaryKeyStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleSelectFieldByPrimaryKeyStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleSelectTotalCountStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleUpdateNoNullFieldStatement;
import com.kabouros.mybatis.core.mapping.handle.HandleUpdateStatement;
import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandle;
import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandleAssembleAdapter;
import com.kabouros.mybatis.core.vendor.Database;
import com.kabouros.mybatis.spring.properties.MybatisKabourosProperties;

/**
 * @author JIANG
 */
@Configuration
@EnableConfigurationProperties(MybatisKabourosProperties.class)
public class DelegatingMybatisKabourosConfigurer implements ApplicationContextAware {
	
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	
	private final MybatisKabourosProperties mybatisKabourosProperties;
	
	private final MybatisKabourosConfigurerComposite configurers = new MybatisKabourosConfigurerComposite();
	
	public DelegatingMybatisKabourosConfigurer(MybatisKabourosProperties mybatisKabourosProperties){
		this.mybatisKabourosProperties = mybatisKabourosProperties;
	}


	@Autowired(required = false)
	public void setConfigurers(List<MybatisKabourosConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addMybatisKabourosConfigurers(configurers);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Bean
	public MappedStatementHandleAssembleAdapter mappedStatementHandleAssembleAdapter(){
		Database database = mybatisKabourosProperties.getDatabase();
		Assert.notNull(database, "Properties mybatis.kabouros.database be null");
		Dialect dialect = database.getDialect();
		List<MappedStatementHandle> newHandles = new ArrayList<>();
		List<MappedStatementHandle> defaultHandles = new ArrayList<>();
		this.configurers.addMappedStatementHandles(newHandles);
		defaultHandles.add(new HandleSelectTotalCountStatement(dialect));
		defaultHandles.add(new HandleDeleteByPrimaryKeysStatement(dialect));
		defaultHandles.add(new HandleDeleteByPrimaryKeyStatement(dialect));
		defaultHandles.add(new HandleInsertAllStatement(dialect));
		defaultHandles.add(new HandleInsertStatement(dialect));
		defaultHandles.add(new HandleSelectAllByPageableStatement(dialect));
		defaultHandles.add(new HandleSelectAllStatement(dialect));
		defaultHandles.add(new HandleSelectByPrimaryKeyStatement(dialect));
		defaultHandles.add(new HandleSelectFieldByPrimaryKeyStatement(dialect));
		defaultHandles.add(new HandleUpdateNoNullFieldStatement(dialect));
		defaultHandles.add(new HandleUpdateStatement(dialect));
		defaultHandles.addAll(newHandles);
		MappedStatementHandleAssembleAdapter mappedStatementHandleAssembleAdapter = new MappedStatementHandleAssembleAdapter(defaultHandles,dialect);
		return mappedStatementHandleAssembleAdapter;
	}
}
