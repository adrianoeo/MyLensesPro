package com.aeo.mylensespro.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.ListReplaceLensBaseAdapter;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.slidetab.SlidingTabLayout;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.TimeLensesVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

public class ListReplaceLensFragment extends ListFragment {

    ListReplaceLensBaseAdapter mListAdapter;
    private Tracker mTracker;

    private ProgressDialog progressDlg;
    private View view;
    private ListView listView;

    private static Boolean isNetworkAvailable;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Retira Tab referente ao Fragment do Periodo das lentes
        DrawerLayout viewMain = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(null);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        Toolbar toolbar = (Toolbar) viewMain.findViewById(R.id.toolbar);

//        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), viewMain, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        viewMain.setDrawerListener(toggle);
        toggle.syncState();

        // Obtain the shared Tracker instance.
        MyLensesApplication application = (MyLensesApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
       TimeLensesVO timeLensesVO = (TimeLensesVO) l.getAdapter().getItem(position);

        if (timeLensesVO != null) {
            TimeLensesFragment fragment = TimeLensesFragment.newInstance(timeLensesVO, false);
            Utility.replaceFragmentWithBackStack(fragment, getFragmentManager());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItemInsert = menu.findItem(R.id.menuInsertLens);
        menuItemInsert.setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuInsertLens:
                TimeLensesFragment fragment = TimeLensesFragment.newInstance(null, true);
                Utility.replaceFragmentWithBackStack(fragment, getFragmentManager());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();
        if (isNetworkAvailable == null) {
            isNetworkAvailable = Utility.isNetworkAvailable(context)
                    /*&& Utility.isConnectionFast(context)*/;
            ListLensesTask task = new ListLensesTask(context);
            task.execute();
        } else {
            if (isNetworkAvailable == Boolean.FALSE && Utility.isNetworkAvailable(context)
                    /*&& Utility.isConnectionFast(context)*/) {
                SyncLensesTask task = new SyncLensesTask(context);
                task.execute();
            } else {
                ListLensesTask task = new ListLensesTask(context);
                task.execute();
            }
            isNetworkAvailable = Utility.isNetworkAvailable(context)
                    /*&&  Utility.isConnectionFast(context)*/;
        }

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Utility.replaceFragmentWithBackStack(new TimeLensesFragment(), getFragmentManager());
            }
        });
        fab.hide();

        mTracker.setScreenName("ListReplaceLensFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();

        progressDlg = null;
    }

    private class SyncLensesTask extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public SyncLensesTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            TimeLensesDAO.getInstance(getContext()).syncTimeLenses();
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(getResources().getString(R.string.sync));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            ListLensesTask task = new ListLensesTask(context);
            task.execute();

        }
    }

    private class ListLensesTask extends AsyncTask<String, Void, List<TimeLensesVO>> {
        private Context context;
//        private ListFragment listFragment;

        public ListLensesTask(Context ctx) {
            context = ctx;
//            this.listFragment = listFragment;
        }

        @Override
        protected List<TimeLensesVO> doInBackground(String... params) {
            return TimeLensesDAO.getInstance(context).getListLenses();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<TimeLensesVO> listLens) {
            if (isAdded()) {
                if (listLens != null && listLens.size() > 0) {
                    mListAdapter = new ListReplaceLensBaseAdapter(context, listLens);
                    setListAdapter(mListAdapter);
                } else {
                    LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            inflater.getContext(),
                            android.R.layout.simple_list_item_1,
                            new String[]{getString(R.string.msg_insert_time_replace)});
                    setListAdapter(adapter);
                }
            }

        }
    }
}
