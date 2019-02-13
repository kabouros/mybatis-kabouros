package com.kabouros.mybatis.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandle;

class MybatisKabourosConfigurerComposite implements MybatisKabourosConfigurer {
	
	private final List<MybatisKabourosConfigurer> delegates = new ArrayList<>();


	public void addMybatisKabourosConfigurers(List<MybatisKabourosConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.delegates.addAll(configurers);
		}
	}


	@Override
	public void addMappedStatementHandles(List<MappedStatementHandle> handles) {
		for (MybatisKabourosConfigurer delegate : this.delegates) {
			delegate.addMappedStatementHandles(handles);
		}
	}

}
