package com.aeo.mylensespro.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.fragment.StatusFragment;
import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.TimeLensesVO;

/**
 * Created by adriano on 07/01/2016.
 */
public class StatusTask extends AsyncTask<String, Void, TimeLensesVO> {

    private StatusFragment statusFragment;
    private ProgressDialog progressDlg;
    private FloatingActionButton fab;
    private int numDays;
    private String sideLens;

    public StatusTask(StatusFragment statusFragment, ProgressDialog progressDlg, FloatingActionButton fab) {
        this.statusFragment = statusFragment;
        this.progressDlg = progressDlg;
        this.fab = fab;
        this.numDays = 0;
        this.sideLens = null;
    }

    public StatusTask(StatusFragment statusFragment, ProgressDialog progressDlg, int numDays,
                      String sideLens) {
        this.statusFragment = statusFragment;
        this.progressDlg = progressDlg;
        this.numDays = numDays;
        this.sideLens = sideLens;
    }

    @Override
    protected TimeLensesVO doInBackground(String... params) {
        return TimeLensesDAO.getInstance(statusFragment.getContext()).getLastLens();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Context context = statusFragment.getContext();

        if (Utility.isNetworkAvailable(context)) {
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(statusFragment.getResources().getString(R.string.loading));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }
    }

    @Override
    protected void onPostExecute(TimeLensesVO timeLensesVO) {
        if (sideLens == null && numDays == 0) {
            if (statusFragment.isAdded()) {
                statusFragment.setDays(timeLensesVO);
                statusFragment.setNumUnitsLenses(timeLensesVO);
                statusFragment.setDaysNotUsed(timeLensesVO);

                if (timeLensesVO == null) {
                    CoordinatorLayout.LayoutParams params
                            = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                    fab.setLayoutParams(params);
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        } else {
            TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(statusFragment.getContext());
            timeLensesDAO.updateDaysNotUsed(numDays, sideLens, timeLensesVO.getId());

            if (sideLens.equals(TimeLensesDAO.LEFT)) {
                timeLensesVO.setNumDaysNotUsedLeft(numDays);
            } else if (sideLens.equals(TimeLensesDAO.RIGHT)) {
                timeLensesVO.setNumDaysNotUsedRight(numDays);
            } else {
                timeLensesVO.setNumDaysNotUsedLeft(numDays);
                timeLensesVO.setNumDaysNotUsedRight(numDays);
            }

            if (statusFragment.isAdded()) {
                statusFragment.setDays(timeLensesVO);
            }
        }
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();
    }
}
