package com.stupid.method.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.stupid.method.reflect.annotation.XClick;
import com.stupid.method.reflect.annotation.XGetValueByView;
import com.stupid.method.reflect.annotation.XInitComplete;
import com.stupid.method.reflect.annotation.XLongClick;
import com.stupid.method.reflect.annotation.XOnCheckedChange;
import com.stupid.method.reflect.annotation.XOnTextChanged;
import com.stupid.method.reflect.annotation.XViewByID;

/**
 * @author wangx
 *
 */
public final class StupidReflect {

	Map<Method, CallMethod> map = new HashMap<Method, StupidReflect.CallMethod>();

	private class CallMethod implements OnCheckedChangeListener,
			OnClickListener, OnLongClickListener,
			android.widget.RadioGroup.OnCheckedChangeListener {
		Method method;

		public CallMethod(Method method) {
			this.method = method;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			viewClickCallMethod(mTarget, buttonView, method);

		}

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			viewClickCallMethod(mTarget, group, method);
		}

		@Override
		public boolean onLongClick(View v) {
			Object result = viewClickCallMethod(mTarget, v, method);
			if (result != null)
				return Boolean.parseBoolean(result.toString());
			else
				return true;
		}

		@Override
		public void onClick(View v) {
			viewClickCallMethod(mTarget, v, method);

		}

	}

	private static String tag = "stupid.refect";

	/**
	 * @param target
	 * @param field
	 * @return
	 */
	private final static Object getField(Object target, Field field) {
		Object result = null;
		try {
			result = field.get(target);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static final Object invoke(Object targetObject, Method method,
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
		} catch (Exception e) {
			Log.e(tag,
					"↓↓↓↓↓↓↓↓↓↓↓↓↓----onClickView-Exception----↓↓↓↓↓↓↓↓↓↓↓↓↓");
			Log.e(tag, method.toGenericString(), e);
			Log.e(tag,
					"↑↑↑↑↑↑↑↑↑↑↑↑↑----onClickView-Exception----↑↑↑↑↑↑↑↑↑↑↑↑↑");
		}
		return null;
	}

	/**
	 * @param target
	 * @param field
	 * @param value
	 * @return
	 */
	private final static boolean setField(Object target, Field field,
			Object value) {
		boolean result = false;
		try {
			field.set(target, value);
			result = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	final Activity fromActivity;

	final View fromView;
	final Resources mResources;

	Object mTarget;

	final String packageName;

	final SparseArray<View> sparseArray = new SparseArray<View>();

	public StupidReflect(Activity activity) {
		mTarget = activity;
		mResources = activity.getResources();
		packageName = activity.getPackageName();
		fromActivity = activity;
		fromView = null;
	}

	public StupidReflect(Object target, View from) {
		mTarget = target;
		mResources = from.getResources();
		packageName = from.getContext().getPackageName();
		fromActivity = null;
		fromView = from;
	}

	/**
	 * 过滤出带注解的属性
	 */
	private void filterField(Object target, Class<?> targetClass) {
		Field[] fields = targetClass.getDeclaredFields();
		for (int i = 0, s = fields.length; i < s; i++) {
			XViewByID byID = fields[i].getAnnotation(XViewByID.class);
			if (byID != null)
				setViewFieldByID(target, fields[i], byID);
		}
		Class<?> superClass = targetClass.getSuperclass();
		if (!Activity.class.equals(superClass)) {
			filterField(target, superClass);
		}
	}

	/**
	 * 过滤出带注解的方法
	 * 
	 * @param target
	 * @param targetClass
	 */
	private List<Method> filtertMethod(Object target, Class<?> targetClass) {
		Method[] methods = targetClass.getDeclaredMethods();
		List<Method> result = new ArrayList<Method>(0);
		for (int i = 0, s = methods.length; i < s; i++) {
			Method meth = methods[i];
			XClick click = meth.getAnnotation(XClick.class);
			XLongClick longClick = meth.getAnnotation(XLongClick.class);
			XOnCheckedChange onChecked = meth
					.getAnnotation(XOnCheckedChange.class);
			XInitComplete complete = meth.getAnnotation(XInitComplete.class);
			XOnTextChanged changed = meth.getAnnotation(XOnTextChanged.class);
			if (click != null) {
				setOnMethodClick(target, meth, click);
			}
			if (longClick != null) {
				setOnMethodLongClick(target, meth, longClick);
			}
			if (onChecked != null) {
				setOnMethodChecked(targetClass, meth, onChecked);
			}
			if (complete != null) {
				result.add(meth);
			}
			if (changed != null) {
				setTextChanged(targetClass, meth, changed);
			}

		}
		return result;
	}

	private void setTextChanged(Class<?> targetClass, final Method method,
			XOnTextChanged changed) {
		View view = findViewById(changed.editTextId());
		if (view != null) {
			final TextView et = (TextView) view;
			et.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					Class<?>[] parameter = method.getParameterTypes();
					Annotation[][] annotations = method
							.getParameterAnnotations();
					if (parameter.length == 0) {
						invoke(mTarget, method);
					} else {
						Object[] para = new Object[parameter.length];
						for (int i = 0; i < para.length; i++) {
							Class<?> cls = parameter[i];
							XGetValueByView valueById = null;
							for (Annotation annotation : annotations[i]) {
								if (annotation.annotationType().equals(
										XGetValueByView.class)) {
									valueById = (XGetValueByView) annotation;
									break;
								}
							}
							if (valueById != null) { // 如果有注解
								View view;
								if (valueById.fromId() == -1)
									view = et;
								else
									view = findViewById(valueById.fromId());
								if (view == null) {
									para[i] = null;
								} else if (View.class.isAssignableFrom(cls)) {
									para[i] = view;
								} else {
									if (!"".equals(valueById.fromMethodName())) {
										Method viewMet = getMethod(
												view.getClass(),
												valueById.fromMethodName());
										para[i] = invoke(view, viewMet);
									} else
										para[i] = ViewTo.toValue(view, cls);
								}
							} else {// 没注解,走基本类型
								para[i] = ViewTo.toValue(et, cls);
							}
						}
						invoke(mTarget, method, para);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

		}

	};

	private void setOnMethodChecked(Object target, Method method,
			XOnCheckedChange longClick) {
		int ids[] = longClick.value();
		CallMethod call = map.get(method);
		if (call == null) {
			call = new CallMethod(method);
			map.put(method, call);
		}
		for (int i = 0; i < ids.length; i++) {
			View view = findViewById(ids[i]);
			if (view != null) {
				Method m = getMethod(view.getClass(),
						"setOnCheckedChangeListener",
						android.widget.RadioGroup.OnCheckedChangeListener.class);
				if (m == null)
					m = getMethod(
							view.getClass(),
							"setOnCheckedChangeListener",
							android.widget.CompoundButton.OnCheckedChangeListener.class);
				if (m != null)
					invoke(view, m, call);
			}
		}
	}

	private static final Method getMethod(Class<?> clz, String name,
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

	private View findView(int id, String idName, String defPackage) {
		if (id == -1) {
			id = mResources.getIdentifier(idName, "id", defPackage);
		}
		return findViewById(id);
	}

	private View findViewById(int id) {
		View view = null;
		if (sparseArray.get(id) != null) {
			view = sparseArray.get(id);
		} else if (fromActivity != null) {
			view = fromActivity.findViewById(id);
		} else if (fromView != null) {
			view = fromView.findViewById(id);
		} else {
			return null;
		}
		sparseArray.put(id, view);
		return view;
	}

	public void init() {
		filterField(mTarget, mTarget.getClass());
		List<Method> mets = filtertMethod(mTarget, mTarget.getClass());
		for (Method method : mets) {

		}
	}

	private void setOnMethodClick(Object target, Method method, XClick click) {
		int ids[] = click.value();
		CallMethod call = map.get(method);
		if (call == null) {
			call = new CallMethod(method);
			map.put(method, call);
		}
		for (int i = 0; i < ids.length; i++) {
			View view = findViewById(ids[i]);
			if (view != null)
				view.setOnClickListener(call);
		}
	}

	private void setOnMethodLongClick(Object target, Method method,
			XLongClick longClick) {

		int ids[] = longClick.value();
		CallMethod call = map.get(method);
		if (call == null) {
			call = new CallMethod(method);
			map.put(method, call);
		}
		for (int i = 0; i < ids.length; i++) {
			View view = findViewById(ids[i]);
			if (view != null)
				view.setOnLongClickListener(call);
		}
	}

	private void setViewFieldByID(Object target, Field field, XViewByID byID) {
		field.setAccessible(true);
		int id = byID.value();
		View view;
		view = findView(id, field.getName(),
				"".equals(byID.defPackage()) ? packageName : byID.defPackage());
		setField(target, field, view);
	}

	private Object viewClickCallMethod(Object targetObject, View v,
			Method method) {
		Class<?>[] parameter = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		Object result = null;
		if (parameter.length == 0) {
			result = invoke(targetObject, method);
		} else {
			Object[] para = new Object[parameter.length];
			for (int i = 0; i < para.length; i++) {
				Class<?> cls = parameter[i];
				XGetValueByView valueById = null;
				for (Annotation annotation : annotations[i]) {
					if (annotation.annotationType().equals(
							XGetValueByView.class)) {
						valueById = (XGetValueByView) annotation;
						break;
					}
				}
				if (valueById != null) { // 如果有注解
					View view;
					if (valueById.fromId() == -1)
						view = v;
					else
						view = findViewById(valueById.fromId());
					if (view == null) {
						para[i] = null;
					} else if (View.class.isAssignableFrom(cls)) {
						para[i] = view;
					} else {
						if (!"".equals(valueById.fromMethodName())) {
							Method viewMet = getMethod(view.getClass(),
									valueById.fromMethodName());
							para[i] = invoke(view, viewMet);
						} else
							para[i] = ViewTo.toValue(view, cls);
					}
				} else {// 没注解,走基本类型
					para[i] = ViewTo.toValue(v, cls);
				}
			}
			result = invoke(targetObject, method, para);
		}
		return result;
	}
}
