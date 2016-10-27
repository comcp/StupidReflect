package com.stupid.method.reflect;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.util.Log;

/**
 * 所有的方法都能使用,即使是私有方法
 * 
 * @author wangx
 *
 */
public class ReflectUtil {

	public static <T> T newInstance(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final Method getMethod(Class<?> clz, String name,
			Class<?>... parameterTypes) {
		try {
			return clz.getMethod(name, parameterTypes);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final Field getDeclaredField(Class<?> clz, String name) {
		Field result = null;
		try {
			result = clz.getDeclaredField(name);
			result.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static final Object getStaticFieldValue(Field field) {
		return getFieldValue(null, field);

	}

	/**
	 * 给静态变量赋值
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static final boolean setStaticFieldValue(Field field, Object value) {
		return setFieldValue(null, field, value);
	}

	/**
	 * 给变量赋值
	 * 
	 * @param target
	 * @param field
	 * @param value
	 * @return
	 */
	public static final boolean setFieldValue(Object target, Field field,
			Object value) {
		try {
			field.setAccessible(true);
			field.set(target, value);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param target
	 * @param field
	 * @return
	 */
	public static final Object getFieldValue(Object target, Field field) {

		try {
			field.setAccessible(true);
			return field.get(target);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 调用静态方法
	 * 
	 * @param method
	 * @param parObjects
	 * @return
	 */
	public static final Object invokeStatic(Method method, Object... parObjects) {
		return invoke(null, method, parObjects);
	}

	/**
	 * 调用方法
	 * 
	 * @param receiver
	 * @param method
	 * @param parObjects
	 * @return
	 */
	public static final Object invoke(Object targetObject, Method method,
			Object... par) {
		try {
			method.setAccessible(true);
			return method.invoke(targetObject, par);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
