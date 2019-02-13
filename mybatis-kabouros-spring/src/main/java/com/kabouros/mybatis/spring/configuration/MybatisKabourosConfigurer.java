package com.kabouros.mybatis.spring.configuration;

import java.util.List;

import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandle;

/**
 * @author JIANG
 */
public interface MybatisKabourosConfigurer {
	
	
	void addMappedStatementHandles(List<MappedStatementHandle> handles);

}
