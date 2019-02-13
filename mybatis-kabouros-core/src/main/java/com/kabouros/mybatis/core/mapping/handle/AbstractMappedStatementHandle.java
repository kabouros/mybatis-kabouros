package com.kabouros.mybatis.core.mapping.handle;

import com.kabouros.mybatis.core.dialect.Dialect;

public abstract class AbstractMappedStatementHandle {
	
	protected final Dialect dialect;
	
	public AbstractMappedStatementHandle(Dialect dialect){
		this.dialect = dialect;
	}
	
}
