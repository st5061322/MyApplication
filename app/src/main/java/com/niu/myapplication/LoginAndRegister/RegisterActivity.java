package com.niu.myapplication.LoginAndRegister;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.niu.myapplication.R;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("註冊");
        auth = FirebaseAuth.getInstance();
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void registerButton(View view){
        final String regUID = ((EditText)findViewById(R.id.regEmail)).getText().toString();
        String regPassd = ((EditText)findViewById(R.id.regPassword)).getText().toString();
        String regconPassd = ((EditText)findViewById(R.id.conPassword)).getText().toString();
        if (regUID.length()!=0 && regPassd.length()!=0) {
            if (regPassd.equals(regconPassd)){
                auth.createUserWithEmailAndPassword(regUID, regPassd)
                        .addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        String message = task.isSuccessful() ? "註冊成功" : "註冊失敗";//重複註冊或沒有網路的視窗未做
                                        if (message.equals("註冊成功")) {
                                            new AlertDialog.Builder(RegisterActivity.this)
                                                    .setMessage(message)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent toLoginAct = new Intent(RegisterActivity.this,LoginActivity.class);
                                                            startActivity(toLoginAct);
                                                        }
                                                    })
                                                    .show();
                                        }else{
                                            new AlertDialog.Builder(RegisterActivity.this)
                                                    .setMessage(message)
                                                    .setPositiveButton("OK",null)
                                                    .show();
                                        }
                                    }
                                });
            }else {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("錯誤!!")
                        .setMessage("第二次密碼與第一次不符!!")
                        .setPositiveButton("OK",null)
                        .show();
            }
        }else {
            new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("錯誤!!")
                    .setMessage("請輸入帳號密碼!!")
                    .setPositiveButton("OK",null)
                    .show();
        }
    }
}
