package com.example.rish.detect_currency;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rish.detect_currency.Modal.LoginResponse;
import com.example.rish.detect_currency.Modal.RegistrationResponse;
import com.example.rish.detect_currency.UserFormActivity;
import com.example.rish.detect_currency.services.RetrtofitInstance;
import com.example.rish.detect_currency.services.SendPhotoService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFormActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button button;
    private EditText name,contact,email,branch,branchcontact,bank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        progressBar = findViewById(R.id.progressbar);
        button = findViewById(R.id.submit_form_bt);
        name=findViewById(R.id.name);
        contact = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        bank=findViewById(R.id.bank);
        branch = findViewById(R.id.branch);
        branchcontact = findViewById(R.id.branch_no);
    }

    public void submitForm(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);

        button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        SendPhotoService sendPhotoService = RetrtofitInstance.getService();
        HashMap<String,String> hmap = new HashMap<>();
        hmap.put("email",email.getText().toString());
        hmap.put("name",name.getText().toString());
        hmap.put("bankname",bank.getText().toString());
        hmap.put("branch",branch.getText().toString());
        hmap.put("contact",contact.getText().toString());
        hmap.put("branchcontact",branchcontact.getText().toString());
        Call<RegistrationResponse> call = sendPhotoService.registerUser(hmap);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                Log.e("Hello", "badiya");
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
                String output = response.body().getIsSuccessfull();
                if(output.equals("true"))
                {
                    Toast.makeText(UserFormActivity.this, "Request sent to admin", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else
                    Toast.makeText(UserFormActivity.this, "Unable to process request", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
                Log.d("activity login", "onFailure:");
                Log.e("sorry", "babes= " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
