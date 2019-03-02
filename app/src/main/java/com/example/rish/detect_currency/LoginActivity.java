package com.example.rish.detect_currency;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
private Button login;
private TextInputEditText email,password;
private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.pass_text);
        signup = findViewById(R.id.btn_signup);
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
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("Activity","Bank_Login");
                    startActivity(i);
                    finish();
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
