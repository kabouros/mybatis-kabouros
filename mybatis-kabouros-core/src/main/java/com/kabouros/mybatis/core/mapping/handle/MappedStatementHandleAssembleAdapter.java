package com.kabouros.mybatis.core.mapping.handle;

import java.util.List;

import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandle;

/**
 * @author JIANG
 */
public class MappedStatementHandleAssembleAdapter {
	
	private final List<MappedStatementHandle> mappedStatementHandles;
	
	public MappedStatementHandleAssembleAdapter(List<MappedStatementHandle> mappedStatementHandles){
		this.mappedStatementHandles = mappedStatementHandles;
	}
	
	
	public List<MappedStatementHandle> getMappedStatementHandles(){
		
		return mappedStatementHandles;
	}
}
