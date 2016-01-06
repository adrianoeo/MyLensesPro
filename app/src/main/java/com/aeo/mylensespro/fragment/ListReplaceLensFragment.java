package com.aeo.mylensespro.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.TextView;

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

    public static List<TimeLensesVO> listLenses;
    ListReplaceLensBaseAdapter mListAdapter;
    public static final String TAG_LENS = "TAG_LENS";
    private Tracker mTracker;

    private ProgressDialog progressDlg;

    private static Boolean isNetworkAvailable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        List<TimeLensesVO> listLens = TimeLensesDAO.getInstance(getContext()).getListLenses();

        List<TimeLensesVO> listLens = TimeLensesDAO.listTimeLensesVO;

        if (listLens != null && listLens.size() == 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.msg_insert_time_replace)});
            setListAdapter(adapter);
        }

        //Retira Tab referente ao Fragment do Periodo das lentes
        View viewMain = getActivity().findViewById(R.id.drawer_layout);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(null);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        Toolbar toolbar = (Toolbar) viewMain.findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
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

        TextView idLens = (TextView) v.findViewById(R.id.textViewIdReplaceLens);

        if (idLens != null) {
//            startActivity(Integer.valueOf(idLens.getText().toString()));
            TimeLensesFragment fragment
                    = TimeLensesFragment.newInstance(idLens.getText().toString(), false);
            Utility.replaceFragmentWithBackStack(fragment, getFragmentManager());
        }
    }

//    private void startActivity(int idLens) {
//        Intent intent = new Intent(getContext(), TimeLensActivityDEL.class);
//        intent.putExtra("ID_LENS", idLens);
//        startActivity(intent);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItemInsert = menu.findItem(R.id.menuInsertLens);
        menuItemInsert.setVisible(true);

//        MenuItem menuItemHelp = menu.findItem(R.id.menuHelp);
//        menuItemHelp.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuInsertLens:
                TimeLensesFragment fragment
                        = TimeLensesFragment.newInstance(null, true);
                Utility.replaceFragmentWithBackStack(fragment, getFragmentManager());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNetworkAvailable == null) {
            isNetworkAvailable = Utility.isNetworkAvailable(getContext());
        } else {
            if (isNetworkAvailable == Boolean.FALSE && Utility.isNetworkAvailable(getContext())) {
                ListLensesTask task = new ListLensesTask();
                task.execute();
            }
            isNetworkAvailable = Utility.isNetworkAvailable(getContext());
        }

        List<TimeLensesVO> listLens = TimeLensesDAO.getInstance(getContext()).getListLenses();

        if (listLens != null && listLens.size() > 0) {
            mListAdapter = new ListReplaceLensBaseAdapter(getContext(), listLens, getFragmentManager());
            setListAdapter(mListAdapter);
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.msg_insert_time_replace)});
            setListAdapter(adapter);
        }

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_new);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.replaceFragmentWithBackStack(new TimeLensesFragment(), getFragmentManager());
            }
        });
        fab.hide();

        mTracker.setScreenName("ListReplaceLensFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private class ListLensesTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            TimeLensesDAO.getInstance(getContext()).syncTimeLenses();
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = new ProgressDialog(getContext());
            progressDlg.setMessage(getResources().getString(R.string.sync));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
        }
    }
}
