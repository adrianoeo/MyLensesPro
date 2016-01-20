package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aeo.mylensespro.R;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ResetPswdActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected Button resetButton;
    protected TextView loginTextView;
    protected TextView signUpTextView;
    private ProgressDialog progressDlg;

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
                    builder.setMessage(R.string.reset_error_message)
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
                startLoginActivity();
//                Intent intent = new Intent(ResetPswdActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                startActivity(intent);
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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void startLoginActivity() {
        Intent intent = new Intent(ResetPswdActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void resetPassword(String userEmail) {
        ResetPswdTask task = new ResetPswdTask(ResetPswdActivity.this, userEmail);
        task.execute();
/*
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
*/
    }

    public class ResetPswdTask extends AsyncTask<String, Void, Void> {
        private Context context;
        private String email;

        public ResetPswdTask(Context context, String email) {
            this.context = context;
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();

            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(getResources().getString(R.string.sending));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                ParseUser.requestPasswordReset(email);
            } catch (ParseException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPswdActivity.this);
                builder.setMessage(R.string.error_reset_pswd)
                        .setTitle(R.string.title_error_reset_pswd)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            progressDlg = null;

            AlertDialog.Builder builder = new AlertDialog.Builder(ResetPswdActivity.this);
            builder.setMessage(R.string.reset_pswd)
                    .setTitle(R.string.title_reset_pswd)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startLoginActivity();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
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
