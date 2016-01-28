package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.task.LogoutTask;
import com.aeo.mylensespro.util.Utility;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    protected EditText usernameEditText;
    protected EditText passwordEditText;
    protected Button loginButton;

    protected TextView signUpTextView;
    protected TextView resetTextView;
    private boolean doubleBackToExitPressedOnce;
    private ProgressDialog progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        signUpTextView = (TextView) findViewById(R.id.signUpText);
        resetTextView = (TextView) findViewById(R.id.resetButton);
        usernameEditText = (EditText) findViewById(R.id.usernameField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    enterLogin();
                    handled = true;
                }
                return handled;
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        resetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPswdActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterLogin();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void enterLogin() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            if (Utility.isNetworkAvailable(LoginActivity.this) &&
                    Utility.isConnectionFast(LoginActivity.this)) {
                setProgressBarIndeterminateVisibility(true);

                login(username, password);
            } else if (Utility.isNetworkAvailable(LoginActivity.this) &&
                    !Utility.isConnectionFast(LoginActivity.this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.login_poor_connection)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                login(username, password);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.not_connected)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void login(String username, String password) {
        LoginTask loginTask = new LoginTask(LoginActivity.this, username, password);
        loginTask.execute();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.msg_press_once_again_to_exit,
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private class LoginTask extends AsyncTask<String, Void, ParseUser> {
        private Context context;
        private String username;
        private String password;

        public LoginTask(Context context, String username, String password) {
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
            progressDlg.setMessage(getResources().getString(R.string.loggingin));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }

        @Override
        protected ParseUser doInBackground(String... params) {
            ParseUser user = null;
            try {
                return ParseUser.logIn(username, password);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(ParseUser parseUser) {
            super.onPostExecute(parseUser);

            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();

            if (parseUser != null && parseUser.getBoolean("emailVerified") == true) {
                // Success!
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (parseUser != null && parseUser.getBoolean("emailVerified") == false){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.login_email_not_confirmed)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();

                if (ParseUser.getCurrentUser() != null) {
                    LogoutTask logoutTask = new LogoutTask(LoginActivity.this, progressDlg, false);
                    logoutTask.execute();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.login_fail_message)
                        .setTitle(R.string.login_error_title)
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
