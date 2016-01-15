package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    protected EditText usernameEditText;
    protected EditText passwordEditText;
    protected Button signUpButton;
    protected TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = (EditText) findViewById(R.id.usernameField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        signUpButton = (Button) findViewById(R.id.signupButton);
        loginTextView = (TextView) findViewById(R.id.link_login);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = username;

                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    setProgressBarIndeterminateVisibility(true);

                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);

                            if (e == null) {
                                // Success!

                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (ParseUser.getCurrentUser() != null) {
                                            ParseUser.logOut();
                                        }
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                };
                                builder.setMessage(R.string.signup_message)
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, onClickListener);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                String msg = null;
                                if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                                    msg = getResources().getString(R.string.signup_error_message);
                                } else if (e.getCode() == ParseException.USERNAME_TAKEN) {
                                    msg = getResources().getString(R.string.signup_error_message_username_already);
                                } else {
                                    msg = e.getMessage();
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(msg)
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
