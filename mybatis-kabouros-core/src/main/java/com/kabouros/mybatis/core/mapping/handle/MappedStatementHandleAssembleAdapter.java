package com.kabouros.mybatis.core.mapping.handle;

import java.util.List;

import com.kabouros.mybatis.core.dialect.Dialect;
import com.kabouros.mybatis.core.mapping.handle.MappedStatementHandle;

/**
 * @author JIANG
 */
public class MappedStatementHandleAssembleAdapter {
	
	private final List<MappedStatementHandle> mappedStatementHandles;
	private final Dialect dialect;
	
	public MappedStatementHandleAssembleAdapter(List<MappedStatementHandle> mappedStatementHandles,Dialect dialect){
		this.mappedStatementHandles = mappedStatementHandles;
		this.dialect = dialect;
	}
	
	
	public List<MappedStatementHandle> getMappedStatementHandles(){
		
		return mappedStatementHandles;
	}


	public Dialect getDialect() {
		return dialect;
	}
	
}
