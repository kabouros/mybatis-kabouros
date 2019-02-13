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
