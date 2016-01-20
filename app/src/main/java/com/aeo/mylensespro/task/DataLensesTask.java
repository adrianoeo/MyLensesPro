package com.aeo.mylensespro.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.DataLensesCollectionPagerAdapter;
import com.aeo.mylensespro.dao.DataLensesDAO;
import com.aeo.mylensespro.fragment.DataLensesFragment;
import com.aeo.mylensespro.vo.DataLensesVO;

/**
 * Created by adriano on 11/01/2016.
 */
public class DataLensesTask extends AsyncTask<String, Void, DataLensesVO> {
    private Context context;
    private ProgressDialog progressDlg;
    DataLensesCollectionPagerAdapter dataLensesCollectionPagerAdapter;
    ViewPager viewPager;
    FragmentManager fragmentManager;
    View view;
    DataLensesFragment fragment;

    public DataLensesTask(Context ctx, ProgressDialog progressDlg,
                          DataLensesCollectionPagerAdapter dataLensesCollectionPagerAdapter,
                          ViewPager viewPager,
                          DataLensesFragment fragment,
                          FragmentManager fragmentManager,
                          View view) {
        context = ctx;
        this.progressDlg = progressDlg;
        this.dataLensesCollectionPagerAdapter = dataLensesCollectionPagerAdapter;
        this.viewPager = viewPager;
        this.fragmentManager = fragmentManager;
        this.view = view;
        this.fragment = fragment;
    }

    @Override
    protected DataLensesVO doInBackground(String... params) {
        return DataLensesDAO.getInstance(context).getLastDataLenses();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();
        progressDlg = new ProgressDialog(context);
        progressDlg.setMessage(fragment.getResources().getString(R.string.loading));
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setIndeterminate(true);
        progressDlg.show();
    }

    @Override
    protected void onPostExecute(DataLensesVO dataLensesVO) {
//        DataLensesDAO.dataLensesVO = dataLensesVO;
        dataLensesCollectionPagerAdapter
                = new DataLensesCollectionPagerAdapter(fragmentManager, context,
                dataLensesVO);

        viewPager = (ViewPager) view.findViewById(R.id.pagerDataLenses);
        viewPager.setAdapter(dataLensesCollectionPagerAdapter);
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();
    }
}
