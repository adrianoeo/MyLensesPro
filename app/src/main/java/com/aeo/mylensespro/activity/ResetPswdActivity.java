package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aeo.mylensespro.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ResetPswdActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected Button resetButton;
    protected TextView loginTextView;
    protected TextView signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pswd);

        emailEditText = (EditText) findViewById(R.id.emailFieldReset);
        resetButton = (Button) findViewById(R.id.resetButton);
        loginTextView = (TextView) findViewById(R.id.link_login);
        signUpTextView = (TextView) findViewById(R.id.signUpText);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                email = email.trim();

                if (email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPswdActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.reset_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    setProgressBarIndeterminateVisibility(true);
                    resetPassword(email);
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPswdActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPswdActivity.this, SignUpActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

    }


//Password Reset
//When you expect users to have passwords to use your application, you should include a way for them to reset their passwords incase they forget them. Parse already has this functionality in place, so you only need to call the following when a user requests a password reset.

    private void resetPassword(String userEmail) {
        ParseUser.requestPasswordResetInBackground(userEmail, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPswdActivity.this);
                    builder.setMessage(R.string.reset_pswd)
                            .setTitle(R.string.title_reset_pswd)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPswdActivity.this);
                    builder.setMessage(R.string.error_reset_pswd)
                            .setTitle(R.string.title_error_reset_pswd)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
