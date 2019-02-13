package com.kabouros.mybatis.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

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

/**
 * @author JIANG
 */
@Configuration
public class DelegatingMybatisKabourosConfigurer implements ApplicationContextAware {
	
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	
	private final MybatisKabourosConfigurerComposite configurers = new MybatisKabourosConfigurerComposite();


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
		List<MappedStatementHandle> newHandles = new ArrayList<>();
		List<MappedStatementHandle> defaultHandles = new ArrayList<>();
		this.configurers.addMappedStatementHandles(newHandles);
		defaultHandles.add(new HandleSelectTotalCountStatement());
		defaultHandles.add(new HandleDeleteByPrimaryKeysStatement());
		defaultHandles.add(new HandleDeleteByPrimaryKeyStatement());
		defaultHandles.add(new HandleInsertAllStatement());
		defaultHandles.add(new HandleInsertStatement());
		defaultHandles.add(new HandleSelectAllByPageableStatement());
		defaultHandles.add(new HandleSelectAllStatement());
		defaultHandles.add(new HandleSelectByPrimaryKeyStatement());
		defaultHandles.add(new HandleSelectFieldByPrimaryKeyStatement());
		defaultHandles.add(new HandleUpdateNoNullFieldStatement());
		defaultHandles.add(new HandleUpdateStatement());
		defaultHandles.addAll(newHandles);
		MappedStatementHandleAssembleAdapter mappedStatementHandleAssembleAdapter = new MappedStatementHandleAssembleAdapter(defaultHandles);
		return mappedStatementHandleAssembleAdapter;
	}
}
