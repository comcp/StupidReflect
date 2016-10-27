package com.stupid.method.reflect.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stupid.method.reflect.StupidReflect;
import com.stupid.method.reflect.annotation.XClick;
import com.stupid.method.reflect.annotation.XGetValueByView;
import com.stupid.method.reflect.annotation.XOnCheckedChange;
import com.stupid.method.reflect.annotation.XOnTextChanged;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new StupidReflect(this).init();
	}

	@XClick({ R.id.submit })
	private void onSubmit(
			@XGetValueByView(fromId = R.id.userId) String userId,
			@XGetValueByView(fromId = R.id.pwd1) String pwd1,
			@XGetValueByView(fromId = R.id.pwd2) String pwd2,
			@XGetValueByView(fromId = R.id.gender, fromMethodName = "getCheckedRadioButtonId") int gender) {

		Toast.makeText(
				this,
				"userId:" + userId + "\r\npwd1:" + pwd1 + "\r\npwd2:" + pwd2
						+ "\r\n性别:" + (gender == R.id.man ? "男" : "女"),

				Toast.LENGTH_SHORT).show();
	}

	@XOnCheckedChange({ R.id.isAdmin })
	private void onCheckCheng(boolean checked,
			@XGetValueByView(fromId = R.id.pwd2) EditText pwd2) {
		pwd2.setText("");
		if (checked) {
			pwd2.setVisibility(View.VISIBLE);
		} else {
			pwd2.setText("");
			pwd2.setVisibility(View.GONE);
		}

	}

	@XOnTextChanged(editTextId = R.id.pwd2)
	private void onText(String txt,
			@XGetValueByView(fromId = R.id.tvMsg) TextView tvMsg) {
		tvMsg.setText(txt);
	}
}
