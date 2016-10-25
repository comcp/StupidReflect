package com.stupid.method.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.view.View;

/**
 * {@link View#setOnLongClickListener(android.view.View.OnLongClickListener)}
 * 
 * @see View.OnLongClickListener#onLongClick(View)
 * @author wangx
 *
 */
@Inherited
@Target(value = { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XLongClick {
	/**
	 * view id
	 * 
	 * @return
	 */
	int[] value();
}
