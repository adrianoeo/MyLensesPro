package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aeo.mylensespro.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ResetPswdActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pswd);

        emailEditText = (EditText) findViewById(R.id.emailFieldReset);
        resetButton = (Button) findViewById(R.id.resetButton);

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
