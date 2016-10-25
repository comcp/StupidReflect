package com.stupid.method.reflect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

@Inherited
@Target(value = { ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XGetValueByView {

	/**
	 * 数据来源view id <br>
	 * fromId=-1 取自当前对象
	 * 
	 * @return
	 */
	int fromId() default -1;

	/**
	 * 只支持无参方法,如果传空,则调用默认值 <br>
	 * int:{@link View#getId()} <br>
	 * boolean : {@link CheckBox#isChecked()}<br>
	 * String : {@link TextView#getText()}<br>
	 * View :findViewById();<br>
	 * 其他 null<br>
	 * 如果没有该方法,则传null
	 * 
	 * @return
	 */
	String fromMethodName() default "";
}
