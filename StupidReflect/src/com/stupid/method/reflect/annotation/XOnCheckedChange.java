package com.stupid.method.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.CheckBox;
import android.widget.RadioGroup;

/**
 * {@linkplain CheckBox#setOnCheckedChangeListener(android.widget.CompoundButton.OnCheckedChangeListener)}
 * 
 * {@linkplain RadioGroup#setOnCheckedChangeListener(android.widget.RadioGroup.OnCheckedChangeListener)}
 * 
 * @see RadioGroup
 * @see
 * 
 * @author wangx
 *
 */
@Inherited
@Target(value = { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XOnCheckedChange {
	int[] value();
}
