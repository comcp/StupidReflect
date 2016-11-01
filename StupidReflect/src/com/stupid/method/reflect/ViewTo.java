package com.stupid.method.reflect;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class ViewTo {
	public static boolean toBoolean(View view) {
		if (view instanceof CheckBox) {
			return ((CheckBox) view).isChecked();
		}
		return false;
	}

	public static Object toValue(View view, Class<?> cls) {

		if (String.class.isAssignableFrom(cls)) {
			return toString(view);
		} else if (int.class.isAssignableFrom(cls)) {

			return view.getId();
		} else if (boolean.class.isAssignableFrom(cls)) {

			return toBoolean(view);
		} else if (View.class.isAssignableFrom(cls)) {
			return view;
		}

		return null;
	}

	private static String toString(View view) {
		if (view instanceof TextView)
			return ((TextView) view).getText().toString();
		else
			return view.toString();
	}
}
