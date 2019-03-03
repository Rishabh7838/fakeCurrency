package com.example.rish.detect_currency;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rish.detect_currency.Modal.LoginResponse;
import com.example.rish.detect_currency.services.LoginService;
import com.example.rish.detect_currency.services.RetrtofitInstance;
import com.example.rish.detect_currency.services.SendPhotoService;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
private Button login;
private TextInputEditText email,password;
private TextView signup;
private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.pass_text);
        signup = findViewById(R.id.btn_signup);
        progressBar=findViewById(R.id.progressbar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().isEmpty())
                    email.setError("Email can't be empty");
                else if(password.getText().toString().isEmpty())
                    password.setError("Password can't be empty");
                else if(emailValidate(email.getText().toString())==false)
                    email.setError("Email should have: abc@xyz.com format");
//                else if(!passwordValidate(password.getText().toString()))
//                    password.setError("Password should have: abc@xyz.com format");
                else {
                    login.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    SendPhotoService sendPhotoService = RetrtofitInstance.getService();
                    HashMap<String,String> hmap = new HashMap<>();
                    hmap.put("email",email.getText().toString());
                    hmap.put("password",password.getText().toString());
                    Call<LoginResponse> call = sendPhotoService.loginUser(hmap);
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            Log.e("Hello", "badiya");
                            progressBar.setVisibility(View.INVISIBLE);
                            login.setEnabled(true);
                            String output = response.body().getIsSuccessfull();
                            if(output.equals("true"))
                            {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("Activity","Bank_Login");
                                startActivityForResult(i,101);
                                finish();
                            }else
                                Toast.makeText(LoginActivity.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            progressBar.setVisibility(View.INVISIBLE);
                            login.setEnabled(true);
                            Log.d("activity login", "onFailure:");
                            Log.e("sorry", "babes= " + t.getMessage());
                        }
                    });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getApplicationContext(), UserFormActivity.class);
                startActivity(signup);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode==101){
                finish();
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean passwordValidate(String s) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(s);

        return matcher.matches();
    }

    private boolean emailValidate(String s) {
        Pattern pattern;
        Matcher matcher;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(s);
        return matcher.matches();

    }

}
