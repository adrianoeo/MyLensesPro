package com.aeo.mylensespro.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.activity.LoginActivity;
import com.parse.ParseUser;

/**
 * Created by adriano on 07/01/2016.
 */
public class LogoutTask extends AsyncTask<String, Void, Boolean> {

    private ProgressDialog progressDlg;
    private Context context;
    private Boolean goToLogin;

    public LogoutTask(Context context, ProgressDialog progressDlg, Boolean goToLogin) {
        this.context = context;
        this.progressDlg = progressDlg;
        this.goToLogin = goToLogin;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        ParseUser.logOut();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();
        progressDlg = new ProgressDialog(context);
        progressDlg.setMessage(context.getResources().getString(R.string.wait));
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setIndeterminate(true);
        progressDlg.show();
    }

    @Override
    protected void onPostExecute(Boolean vboolean) {
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();

        if (goToLogin) {
            Intent intent = new Intent(context, LoginActivity.class);
        /*So ‘back’ is pressed, the app navigates to the previous activity.
          We need to clear the stack history and set the LoginActivity as the start of
          the history stack. Modify loadLoginView() as shown.*/
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }
}
