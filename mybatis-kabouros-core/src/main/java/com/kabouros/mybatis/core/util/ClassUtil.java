/**
 * MIT License
 * Copyright (c) 2018 jiangcihuo
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
package com.kabouros.mybatis.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author JIANG
 */
public class ClassUtil {
	
	
	public static Class<?> getGenericReturnType(Method method) {
		Type genericType = method.getGenericReturnType();
		if(genericType instanceof ParameterizedType){
			return (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
		}
	    throw new IllegalArgumentException(String.join("",method.getDeclaringClass().getName(), ".",method.getName()," return type not generic type."));
	}
	
	public static Class<?> getInterfaceGeneric(Class<?> declaringClass,int index){
    	Type genericSuperclass = declaringClass.getGenericSuperclass();
    	if(null != genericSuperclass && genericSuperclass instanceof ParameterizedType){
    		ParameterizedType type = (ParameterizedType)genericSuperclass;
    		Type[] actualTypeArguments = type.getActualTypeArguments();
    		if(actualTypeArguments.length >= index){
    		    return (Class<?>)actualTypeArguments[index];
    		}
    	}else{
    		Type[] genericInterfaces = declaringClass.getGenericInterfaces();
        	if(null != genericInterfaces && genericInterfaces.length != 0){
        		if(genericInterfaces[0] instanceof ParameterizedType){
        			ParameterizedType type = (ParameterizedType)genericInterfaces[0];
        			Type[] actualTypeArguments = type.getActualTypeArguments();
            		if(actualTypeArguments.length >= index){
            			return (Class<?>)actualTypeArguments[index];
            		}
        		}
        	}
    	}
    	return null;
	}
	
	public static boolean isBaseType(Class<?> clazz) {
	    if (clazz.equals(java.lang.Character.class) ||
	        clazz.equals(java.lang.Integer.class) || clazz.equals(int.class) ||
	    	clazz.equals(java.lang.Long.class) || clazz.equals(long.class) ||
	        clazz.equals(java.lang.Double.class) || clazz.equals(double.class) ||
	        clazz.equals(java.lang.Float.class) || clazz.equals(float.class) ||
	        clazz.equals(java.lang.Short.class) || clazz.equals(short.class) ||
	        clazz.equals(java.lang.Byte.class) || clazz.equals(byte.class) ||
	        clazz.equals(java.lang.Boolean.class) || clazz.equals(boolean.class)) {
	        return true;
	    }
	    return false;
	}

}
