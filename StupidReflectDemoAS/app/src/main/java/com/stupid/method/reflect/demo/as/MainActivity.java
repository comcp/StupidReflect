package com.stupid.method.reflect.demo.as;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stupid.method.reflect.StupidReflect;
import com.stupid.method.reflect.annotation.XClick;
import com.stupid.method.reflect.annotation.XGetValueByView;
import com.stupid.method.reflect.annotation.XOnCheckedChange;
import com.stupid.method.reflect.annotation.XOnTextChanged;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        new StupidReflect(this).init();
    }

    @XOnCheckedChange({R.id.isAdmin})
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

    @XOnCheckedChange({R.id.gender})
    private void onRadioButtonChange(@XGetValueByView(fromId = R.id.gender, fromMethodName = "getCheckedRadioButtonId") int changeId) {
        switch (changeId) {
            case R.id.woman:
                Toast.makeText(this, "选择->女", Toast.LENGTH_SHORT).show();
                break;
            case R.id.man:
                Toast.makeText(this, "选择->男", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    @XOnTextChanged(editTextId = R.id.pwd2)
    private void onText(String txt,
                        @XGetValueByView(fromId = R.id.tvMsg) TextView tvMsg) {
        tvMsg.setText(txt);
    }

    @XClick({R.id.submit})
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
}
