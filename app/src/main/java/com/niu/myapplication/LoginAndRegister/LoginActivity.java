package com.niu.myapplication.LoginAndRegister;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.niu.myapplication.R;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText userAccount,userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userAccount = (EditText)findViewById(R.id.editAccout);
        userPassword = (EditText)findViewById(R.id.editPassword);
        SharedPreferences setting = getSharedPreferences("appSetting",MODE_PRIVATE);
        userAccount.setText(setting.getString("PREF_ACCOUNT",""));
        userPassword.setText(setting.getString("PREF_PASSWORD",""));
        auth = FirebaseAuth.getInstance();
    }
    public void login(View view) {
        final String account = ((EditText) findViewById(R.id.editAccout)).getText().toString();
        final String password = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        if (!account.isEmpty() && !password.isEmpty()) {
            auth.signInWithEmailAndPassword(account, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences setting = getSharedPreferences("appSetting", MODE_PRIVATE);
                                setting.edit()
                                        .putString("PREF_ACCOUNT", account)
                                        .putString("PREF_PASSWORD", password)
                                        .apply();
                                finish();
                            }else {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("錯誤!!")
                                        .setMessage("帳號或密碼錯誤")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }
                    });
        }else {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("錯誤!!")
                    .setMessage("請輸入帳號密碼")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void register(View view){
        Intent intentReg = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intentReg);
    }

    public void ForgetPassword(View view){
        final View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.alert_main, null);
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("請輸入您的Email，以重設密碼")
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) v.findViewById(R.id.edittext);
                        String email = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplication(), "請輸入您的Email!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(
                                            LoginActivity.this,
                                            "郵件發送成功請到電子信箱重設您的密碼",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivity.this,
                                            "重設密碼郵件發送失敗",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }
}
