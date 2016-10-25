package com.stupid.method.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(value = { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XClick {
	/** 目标方法名 **/
	int[] value();
}
