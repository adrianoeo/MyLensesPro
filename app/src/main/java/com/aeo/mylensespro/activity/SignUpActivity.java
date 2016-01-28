package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.task.LogoutTask;
import com.aeo.mylensespro.util.Utility;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {

    protected EditText usernameEditText;
    protected EditText passwordEditText;
    protected Button signUpButton;
    protected TextView loginTextView;
    private ProgressDialog progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        usernameEditText = (EditText) findViewById(R.id.usernameField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        signUpButton = (Button) findViewById(R.id.signupButton);
        loginTextView = (TextView) findViewById(R.id.link_login);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    enterSignup();
                    handled = true;
                }
                return handled;
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSignup();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void enterSignup() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage(R.string.signup_error_message)
                    .setTitle(R.string.signup_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            if (Utility.isNetworkAvailable(SignUpActivity.this) &&
                    Utility.isConnectionFast(SignUpActivity.this)) {
                setProgressBarIndeterminateVisibility(true);

                signup(username, password);
            } else if (Utility.isNetworkAvailable(SignUpActivity.this) &&
                    !Utility.isConnectionFast(SignUpActivity.this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(R.string.signup_poor_connection)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signup(username, password);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(R.string.not_connected)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void signup(String username, String password) {
        SignupTask signupTask = new SignupTask(SignUpActivity.this, username, password);
        signupTask.execute();
    }

    public class SignupTask extends AsyncTask<String, Void, String> {
        private Context context;
        private String username;
        private String password;

        public SignupTask(Context context, String username, String password) {
            this.context = context;
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();

            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(getResources().getString(R.string.signing_up));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }

        @Override
        protected String doInBackground(String... params) {
            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(username);
            String msg = null;
            try {
                newUser.signUp();

            } catch (ParseException e) {
                if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                    msg = getResources().getString(R.string.signup_error_message);
                } else if (e.getCode() == ParseException.USERNAME_TAKEN) {
                    msg = getResources().getString(R.string.signup_error_message_username_already);
                } else {
                    msg = e.getMessage();
                }
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            super.onPostExecute(msg);

            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            progressDlg = null;

            if (msg == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ParseUser.getCurrentUser() != null) {
                            LogoutTask logoutTask = new LogoutTask(SignUpActivity.this, progressDlg, true);
                            logoutTask.execute();
                        }
                    }
                };
                builder.setMessage(R.string.signup_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, onClickListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(msg)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();

        progressDlg = null;
    }
}
