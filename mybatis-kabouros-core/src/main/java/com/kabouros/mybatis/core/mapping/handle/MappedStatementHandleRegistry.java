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
package com.kabouros.mybatis.core.mapping.handle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author JIANG
 */
public class MappedStatementHandleRegistry {
	
	private static final Map<Class<? extends MappedStatementHandle>,MappedStatementHandle> HANDLE_MAP = new HashMap<>();
	
	static {
		HANDLE_MAP.put(HandleInsertStatement.class, new HandleInsertStatement());
		HANDLE_MAP.put(HandleInsertAllStatement.class, new HandleInsertAllStatement());
		HANDLE_MAP.put(HandleUpdateStatement.class, new HandleUpdateStatement());
		HANDLE_MAP.put(HandleUpdateNoNullFieldStatement.class, new HandleUpdateNoNullFieldStatement());
		HANDLE_MAP.put(HandleSelectByPrimaryKeyStatement.class, new HandleSelectByPrimaryKeyStatement());
		HANDLE_MAP.put(HandleSelectFieldByPrimaryKeyStatement.class, new HandleSelectFieldByPrimaryKeyStatement());
		HANDLE_MAP.put(HandleDeleteByPrimaryKeysStatement.class, new HandleDeleteByPrimaryKeysStatement());
		HANDLE_MAP.put(HandleDeleteByPrimaryKeyStatement.class, new HandleDeleteByPrimaryKeyStatement());
	}
	
	/**
	 * read-only
	 * @return mappedStatement handles
	 */
	public List<MappedStatementHandle> getRegistry(){
		
		return Collections.unmodifiableList(HANDLE_MAP.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
	}
	
	public static final MappedStatementHandleRegistry getInstance(){
		
		return MappedStatementHandleRegistryHoler.INSTANCE;
	}
	
	private static class MappedStatementHandleRegistryHoler{
		
		private static final MappedStatementHandleRegistry INSTANCE = new MappedStatementHandleRegistry();
	}
}
