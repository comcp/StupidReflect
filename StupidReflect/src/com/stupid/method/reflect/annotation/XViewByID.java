package com.stupid.method.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XViewByID {

	/**
	 * view id
	 * 
	 * @return
	 */
	int value() default -1;

	/**
	 * 资源包名
	 * 
	 * @return
	 */
	String defPackage() default "";

}
