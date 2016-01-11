package com.aeo.mylensespro.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.LensesDataDAO;
import com.aeo.mylensespro.fragment.DataLensesFragment;
import com.aeo.mylensespro.vo.DataLensesVO;

/**
 * Created by adriano on 11/01/2016.
 */
public class DataLensesTask extends AsyncTask<String, Void, DataLensesVO> {
    private Context context;
    private ProgressDialog progressDlg;
    DataLensesFragment fragment;

    public DataLensesTask(Context ctx, ProgressDialog progressDlg,
                          DataLensesFragment fragment) {
        context = ctx;
        this.fragment = fragment;
        this.progressDlg = progressDlg;
    }

    @Override
    protected DataLensesVO doInBackground(String... params) {
        return LensesDataDAO.getInstance(context).getLastDataLenses();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDlg = new ProgressDialog(context);
        progressDlg.setMessage(fragment.getResources().getString(R.string.loading));
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setIndeterminate(true);
        progressDlg.show();
    }

    @Override
    protected void onPostExecute(DataLensesVO dataLensesVO) {
        fragment.dataLensesVO = dataLensesVO;
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();
    }
}
