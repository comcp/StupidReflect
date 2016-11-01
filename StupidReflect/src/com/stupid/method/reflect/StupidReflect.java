package com.stupid.method.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
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

	private static final class Reflect {

		List<Field> mFields = new ArrayList<Field>();
		List<Method> mMethods = new ArrayList<Method>();
		final Reflect parent;
		private static final Map<Class<?>, Reflect> mCache = new HashMap<Class<?>, StupidReflect.Reflect>(
				3);

		private static Reflect getCache(Class<?> clz) {
			Reflect ref = mCache.get(clz);
			return ref == null ? new Reflect(clz) : ref;
		}

		private Reflect(Class<?> clz) {
			mCache.put(clz, this);
			initFiltertField(clz);
			initFiltertMethod(clz);
			Class<?> supclz = clz.getSuperclass();
			if (supclz != null) {

				if (Activity.class.equals(supclz)) {
					parent = null;
					return;
				} else if (Object.class.equals(supclz)) {
					parent = null;
					return;
				}
				Reflect tmpParent = mCache.get(clz.getSuperclass());
				if (tmpParent == null)
					tmpParent = new Reflect(clz.getSuperclass());
				parent = tmpParent;

			} else
				parent = null;
		}

		/**
		 * 过滤出带注解的属性
		 */
		private void initFiltertField(Class<?> targetClass) {
			Field[] fields = targetClass.getDeclaredFields();
			for (int i = 0, s = fields.length; i < s; i++) {
				XViewByID byID = fields[i].getAnnotation(XViewByID.class);
				if (byID != null)
					mFields.add(fields[i]);
			}
		}

		static Class[] ANNOTATION_METHOD = new Class[] { XClick.class,
				XLongClick.class, XOnCheckedChange.class, XOnTextChanged.class };

		/**
		 * 过滤出带注解的方法
		 * 
		 * @param target
		 * @param targetClass
		 */
		private void initFiltertMethod(Class<?> targetClass) {
			Method[] methods = targetClass.getDeclaredMethods();
			for (int i = 0, s = methods.length; i < s; i++) {
				Method meth = methods[i];
				for (Class clz : ANNOTATION_METHOD) {
					Object obj2 = meth.getAnnotation(clz);
					if (obj2 != null) {
						mMethods.add(meth);
						break;
					}
				}
			}
		}

	}

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
	private void filterField(Reflect mReflect, Object target) {
		if (mReflect.parent != null)
			filterField(mReflect.parent, target);
		List<Field> fields = mReflect.mFields;
		for (Field field : fields) {
			XViewByID byID = field.getAnnotation(XViewByID.class);
			if (byID != null)
				setViewFieldByID(target, field, byID);
		}

	}

	/**
	 * 过滤出带注解的方法
	 * 
	 * @param target
	 * @param targetClass
	 * @return
	 */
	private void filtertMethod(Reflect mReflect, Object target) {
		if (mReflect.parent != null)
			filtertMethod(mReflect.parent, target);
		List<Method> methods = mReflect.mMethods;
		for (Method meth : methods) {
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
				setOnMethodChecked(meth, onChecked);
			}

			if (changed != null) {
				setTextChanged(meth, changed);
			}

		}
	}

	private void setTextChanged(final Method method, XOnTextChanged changed) {
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
						ReflectUtil.invoke(mTarget, method);
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

										Method viewMet = ReflectUtil.getMethod(
												view.getClass(),
												valueById.fromMethodName());
										para[i] = ReflectUtil.invoke(view,
												viewMet);
									} else
										para[i] = ViewTo.toValue(view, cls);
								}
							} else {// 没注解,走基本类型
								para[i] = ViewTo.toValue(et, cls);
							}
						}
						ReflectUtil.invoke(mTarget, method, para);
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

	private Object getInvokeMethod(Object target, String... methodname) {
		if (methodname.length > 0) {
			Method viewMet = ReflectUtil.getMethod(target.getClass(),
					methodname[0]);
			Object result = ReflectUtil.invoke(target, viewMet);
			if (methodname.length == 1)
				return result;
			else if (result == null)
				return null;
			else {
				String[] destPos = new String[methodname.length - 1];
				System.arraycopy(methodname, 1, destPos, 0, destPos.length);
				return getInvokeMethod(result, destPos);
			}
		} else {
			return null;
		}
	}

	private void setOnMethodChecked(Method method, XOnCheckedChange longClick) {
		int ids[] = longClick.value();
		CallMethod call = map.get(method);
		if (call == null) {
			call = new CallMethod(method);
			map.put(method, call);
		}
		for (int i = 0; i < ids.length; i++) {
			View view = findViewById(ids[i]);
			if (view != null) {
				Method m = ReflectUtil
						.getMethod(
								view.getClass(),
								"setOnCheckedChangeListener",
								android.widget.RadioGroup.OnCheckedChangeListener.class);
				if (m == null)
					m = ReflectUtil
							.getMethod(
									view.getClass(),
									"setOnCheckedChangeListener",
									android.widget.CompoundButton.OnCheckedChangeListener.class);
				if (m != null)
					ReflectUtil.invoke(view, m, call);
			}
		}
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
		Reflect mReflect = Reflect.getCache(mTarget.getClass());
		filterField(mReflect, mTarget);
		filtertMethod(mReflect, mTarget);
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
		ReflectUtil.setFieldValue(target, field, view);
	}

	private Object viewClickCallMethod(Object targetObject, View v,
			Method method) {
		Class<?>[] parameter = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		Object result = null;
		if (parameter.length == 0) {
			result = ReflectUtil.invoke(targetObject, method);
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
					View view;// 获取数据的目标
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
							String[] methods = valueById.fromMethodName()
									.trim().split("#");
							para[i] = getInvokeMethod(view, methods);
						} else
							para[i] = ViewTo.toValue(view, cls);
					}
				} else {// 没注解,走基本类型
					para[i] = ViewTo.toValue(v, cls);
				}
			}
			result = ReflectUtil.invoke(targetObject, method, para);
		}
		return result;
	}
}
