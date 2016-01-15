package com.aeo.mylensespro.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.DataLensesCollectionPagerAdapter;
import com.aeo.mylensespro.dao.DataLensesDAO;
import com.aeo.mylensespro.slidetab.SlidingTabLayout;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.DataLensesVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link DataLensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataLensesFragment extends Fragment {
    DataLensesCollectionPagerAdapter dataLensesCollectionPagerAdapter;
    ViewPager mViewPager;

    private MenuItem menuItemEdit;
    private MenuItem menuItemSave;
    private MenuItem menuItemCancel;
    private MenuItem menuItemShare;

    private static TextView textViewIdDataLens;
    private static TextView textViewObjectIdDataLens;
    private static EditText editTextDescLeft;
    private static AutoCompleteTextView editTextBrandLeft;
    private static EditText editTextBuySiteLeft;
    private static Spinner spinnerTypeLensLeft;
    private static Spinner spinnerPowerLeft;
    private static Spinner spinnerAddLeft;
    private static Spinner spinnerAxisLeft;
    private static Spinner spinnerCylinderLeft;
    private static EditText editTextBCLeft;
    private static EditText editTextDiaLeft;

    private static EditText editTextDescRight;
    private static AutoCompleteTextView editTextBrandRight;
    private static EditText editTextBuySiteRight;
    private static Spinner spinnerTypeLensRight;
    private static Spinner spinnerPowerRight;
    private static Spinner spinnerAddRight;
    private static Spinner spinnerAxisRight;
    private static Spinner spinnerCylinderRight;
    private static EditText editTextBCRight;
    private static EditText editTextDiaRight;

    private LinearLayout layoutLeft;
    private LinearLayout layoutRight;
    private ShareActionProvider mShareActionProvider;

    private static boolean isSaveVisible;
    private Tracker mTracker;
    private ProgressDialog progressDlg;
    private ProgressDialog progressDlgSync;

//    private static DataLensesVO dataLensesVO;

    private View view;
    private static Boolean isNetworkAvailable;

    public DataLensesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idLenses idLenses.
     * @return A new instance of fragment TimeLensesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataLensesFragment newInstance(int idLenses) {
        DataLensesFragment fragment = new DataLensesFragment();
//        Bundle args = new Bundle();
//        args.putInt(KEY_ID_LENS, idLenses);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_data_lenses, container, false);

        dataLensesCollectionPagerAdapter
                = new DataLensesCollectionPagerAdapter(getFragmentManager(), getContext(),
                null);

        mViewPager = (ViewPager) view.findViewById(R.id.pagerDataLenses);
        mViewPager.setAdapter(dataLensesCollectionPagerAdapter);

        View viewMain = getActivity().findViewById(R.id.drawer_layout);

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);
        mSlidingTabLayout.setViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        isSaveVisible = false;

        // Obtain the shared Tracker instance.
        MyLensesApplication application = (MyLensesApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.data_lenses, menu);
        menuItemEdit = menu.findItem(R.id.menuEditDataLenses);
        menuItemSave = menu.findItem(R.id.menuSaveDataLenses);
        menuItemCancel = menu.findItem(R.id.menuCancelDataLenses);
        menuItemShare = menu.findItem(R.id.menuShareDataLenses);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItemShare);
//        mShareActionProvider.setShareIntent(getDefaultIntent());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        enableMenuSaveCancel(isSaveVisible);
        enableMenuEdit(!isSaveVisible);
        super.onPrepareOptionsMenu(menu);
    }

    private void enableMenuEdit(boolean enabled) {
        if (menuItemEdit != null) {
            menuItemEdit.setVisible(enabled);
        }
        if (menuItemShare != null) {
            menuItemShare.setVisible(enabled);
        }
    }

    private void enableMenuSaveCancel(boolean enabled) {
        if (menuItemSave != null) {
            menuItemSave.setVisible(enabled);
        }
        if (menuItemCancel != null) {
            menuItemCancel.setVisible(enabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment leftFragment = dataLensesCollectionPagerAdapter.getFragment(0);
        Fragment rightFragment = dataLensesCollectionPagerAdapter.getFragment(1);

        if (leftFragment != null && rightFragment != null) {

            View leftView = leftFragment.getView();
            View rightView = rightFragment.getView();

            layoutLeft = (LinearLayout) leftView.findViewById(R.id.id_layout_Lens_left);
            layoutRight = (LinearLayout) rightView.findViewById(R.id.id_layout_Lens_right);

            switch (item.getItemId()) {
                case R.id.menuEditDataLenses:
                    enableControls(true, layoutLeft);
                    enableControls(true, layoutRight);
                    enableMenuEdit(false);
                    enableMenuSaveCancel(true);

                    editTextBrandLeft = (AutoCompleteTextView) leftView.findViewById(R.id.editTextLeftBrand);
                    editTextBrandLeft.setFocusable(true);
                    editTextBrandLeft.setFocusableInTouchMode(true);
                    isSaveVisible = true;
                    return true;
                case R.id.menuSaveDataLenses:
                    enableControls(false, layoutLeft);
                    enableControls(false, layoutRight);
                    save();
                    enableMenuEdit(true);
                    enableMenuSaveCancel(false);
                    isSaveVisible = false;
                    return true;
                case R.id.menuCancelDataLenses:
                    enableControls(false, layoutLeft);
                    enableControls(false, layoutRight);
                    enableMenuEdit(true);
                    enableMenuSaveCancel(false);
                    isSaveVisible = false;
                    return true;
                case android.R.id.home:
                    isSaveVisible = false;
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    private void enableControls(boolean enabled, ViewGroup view) {
        if (view != null) {
            for (int i = 0; i < view.getChildCount(); i++) {
                View child = view.getChildAt(i);
                child.setEnabled(enabled);
                if (child instanceof ViewGroup) {
                    enableControls(enabled, (ViewGroup) child);
                }
            }
        }
    }

    private boolean getViewsFragmentLenses() {
        Fragment leftFragment = dataLensesCollectionPagerAdapter.getFragment(0);

        if (leftFragment != null) {
            View leftView = leftFragment.getView();

            if (leftView != null) {
                textViewIdDataLens = (TextView) leftView.findViewById(R.id.textViewIdDataLens);
                textViewObjectIdDataLens = (TextView) leftView.findViewById(R.id.textViewObjectIdDataLens);
                editTextDescLeft = (EditText) leftView.findViewById(R.id.EditTextDescLeft);
                editTextBrandLeft = (AutoCompleteTextView) leftView.findViewById(R.id.editTextLeftBrand);
                editTextBuySiteLeft = (EditText) leftView.findViewById(R.id.EditTextBuySiteLeft);
                spinnerTypeLensLeft = (Spinner) leftView.findViewById(R.id.spinnerTypeLensLeft);
                spinnerPowerLeft = (Spinner) leftView.findViewById(R.id.spinnerPowerLeft);
                spinnerAddLeft = (Spinner) leftView.findViewById(R.id.spinnerAddLeft);
                spinnerAxisLeft = (Spinner) leftView.findViewById(R.id.spinnerAxisLeft);
                spinnerCylinderLeft = (Spinner) leftView.findViewById(R.id.spinnerCylinderLeft);
                editTextBCLeft = (EditText) leftView.findViewById(R.id.editTextBCLeft);
                editTextDiaLeft = (EditText) leftView.findViewById(R.id.editTextDiaLeft);
            }
        }

        Fragment rightFragment = dataLensesCollectionPagerAdapter.getFragment(1);

        if (rightFragment != null) {
            View rightView = rightFragment.getView();

            if (rightView != null) {
                editTextDescRight = (EditText) rightView.findViewById(R.id.EditTextDescRight);
                editTextBrandRight = (AutoCompleteTextView) rightView.findViewById(R.id.editTextRightBrand);
                editTextBuySiteRight = (EditText) rightView.findViewById(R.id.EditTextBuySiteRight);
                spinnerTypeLensRight = (Spinner) rightView.findViewById(R.id.spinnerTypeLensRight);
                spinnerPowerRight = (Spinner) rightView.findViewById(R.id.spinnerPowerRight);
                spinnerAddRight = (Spinner) rightView.findViewById(R.id.spinnerAddRight);
                spinnerAxisRight = (Spinner) rightView.findViewById(R.id.spinnerAxisRight);
                spinnerCylinderRight = (Spinner) rightView.findViewById(R.id.spinnerCylinderRight);
                editTextBCRight = (EditText) rightView.findViewById(R.id.editTextBCRight);
                editTextDiaRight = (EditText) rightView.findViewById(R.id.editTextDiaRight);
            }
        }

        return leftFragment != null && rightFragment != null;
    }

    // Call to update the share intent
//    private void setShareIntent(Intent shareIntent) {
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(shareIntent);
//        }
//    }

    private Intent getDefaultIntent(DataLensesVO dataLensesVO) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.str_email_subject_data_lenses));
        intent.putExtra(Intent.EXTRA_TEXT, getDataLensesToShare(dataLensesVO));

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());

        return intent;
    }

    private String getDataLensesToShare(DataLensesVO dataLensesVO) {
//        DataLensesDAO dao = DataLensesDAO.getInstance(getContext());
//        DataLensesVO dataLensesVO = dao.getLastDataLenses();
//        DataLensesVO dataLensesVO = DataLensesDAO.dataLensesVO;


        StringBuilder text = new StringBuilder();

        if (dataLensesVO != null) {
            text.append("\n");
            text.append("- ").append(getString(R.string.tabLeftLens))
                    .append("\n\n");
            text.append(getLeftText(dataLensesVO)).append("\n\n");
            text.append("- ").append(getString(R.string.tabRightLens))
                    .append("\n\n");
            text.append(getRightText(dataLensesVO)).append("\n");
        }

        return text.toString();
    }

    private String getLeftText(DataLensesVO vo) {
        String typeLensLeft = vo.getTypeLeft() == null ? "" : getResources()
                .getStringArray(R.array.array_type_lens)[vo.getTypeLeft()];

        String powerLeft = vo.getPowerLeft() == null ? "" : getResources()
                .getStringArray(R.array.array_power)[vo.getPowerLeft()];

        String addLeft = vo.getAddLeft() == null ? "" : getResources()
                .getStringArray(R.array.array_add)[vo.getAddLeft()];

        String axisLeft = vo.getAxisLeft() == null ? "" : getResources()
                .getStringArray(R.array.array_axis)[vo.getAxisLeft()];

        String cylinderLeft = vo.getCylinderLeft() == null ? "" : getResources()
                .getStringArray(R.array.array_cylinder)[vo.getCylinderLeft()];

        StringBuilder sbLeft = new StringBuilder();
        sbLeft.append(getString(R.string.lbl_brand)).append(": ")
                .append(vo.getBrandLeft() == null ? "" : vo.getBrandLeft())
                .append("\n");
        sbLeft.append(getString(R.string.lbl_desc_lens))
                .append(": ")
                .append(vo.getDescriptionLeft() == null ? "" : vo
                        .getDescriptionLeft()).append("\n");
        sbLeft.append(getString(R.string.lbl_type_lens)).append(": ")
                .append(typeLensLeft).append("\n");

        // Miophya/Hipermetrophya
        if (vo.getTypeLeft() != null) {
            if (0 == vo.getTypeLeft()) {
                sbLeft.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerLeft).append("\n");
                // Astigmatism
            } else if (1 == vo.getTypeLeft()) {
                sbLeft.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerLeft).append("\n");
                sbLeft.append(getString(R.string.lbl_cylinder)).append(": ")
                        .append(cylinderLeft).append("\n");
                sbLeft.append(getString(R.string.lbl_axis)).append(": ")
                        .append(axisLeft).append("\n");
                // Multifocal/Presbyopia
            } else if (2 == vo.getTypeLeft()) {
                sbLeft.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerLeft).append("\n");
                sbLeft.append(getString(R.string.lbl_add)).append(": ")
                        .append(addLeft).append("\n");
            }
        }
        sbLeft.append(getString(R.string.lbl_bc)).append(": ")
                .append(vo.getBcLeft() == null ? "" : vo.getBcLeft())
                .append("\n");
        sbLeft.append(getString(R.string.lbl_dia)).append(": ")
                .append(vo.getDiaLeft() == null ? "" : vo.getDiaLeft())
                .append("\n");
        return sbLeft.toString();
    }

    private String getRightText(DataLensesVO vo) {
        String typeLensRight = vo.getTypeRight() == null ? "" : getResources()
                .getStringArray(R.array.array_type_lens)[vo.getTypeRight()];

        String powerRight = vo.getPowerRight() == null ? "" : getResources()
                .getStringArray(R.array.array_power)[vo.getPowerRight()];

        String addRight = vo.getAddRight() == null ? "" : getResources()
                .getStringArray(R.array.array_add)[vo.getAddRight()];

        String axisRight = vo.getAxisRight() == null ? "" : getResources()
                .getStringArray(R.array.array_axis)[vo.getAxisRight()];

        String cylinderRight = vo.getCylinderRight() == null ? "" : getResources()
                .getStringArray(R.array.array_cylinder)[vo.getCylinderRight()];

        StringBuilder sbRight = new StringBuilder();
        sbRight.append(getString(R.string.lbl_brand)).append(": ")
                .append(vo.getBrandRight() == null ? "" : vo.getBrandRight())
                .append("\n");
        sbRight.append(getString(R.string.lbl_desc_lens))
                .append(": ")
                .append(vo.getDescriptionRight() == null ? "" : vo
                        .getDescriptionRight()).append("\n");
        sbRight.append(getString(R.string.lbl_type_lens)).append(": ")
                .append(typeLensRight).append("\n");

        // Miophya/Hipermetrophya
        if (vo.getTypeRight() != null) {
            if (0 == vo.getTypeRight()) {
                sbRight.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerRight).append("\n");
                // Astigmatism
            } else if (1 == vo.getTypeRight()) {
                sbRight.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerRight).append("\n");
                sbRight.append(getString(R.string.lbl_cylinder)).append(": ")
                        .append(cylinderRight).append("\n");
                sbRight.append(getString(R.string.lbl_axis)).append(": ")
                        .append(axisRight).append("\n");
                // Multifocal/Presbyopia
            } else if (2 == vo.getTypeRight()) {
                sbRight.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerRight).append("\n");
                sbRight.append(getString(R.string.lbl_add)).append(": ")
                        .append(addRight).append("\n");
            }
        }
        sbRight.append(getString(R.string.lbl_bc)).append(": ")
                .append(vo.getBcRight() == null ? "" : vo.getBcRight())
                .append("\n");
        sbRight.append(getString(R.string.lbl_dia)).append(": ")
                .append(vo.getDiaRight() == null ? "" : vo.getDiaRight())
                .append("\n");

        return sbRight.toString();
    }

    private void save() {
        SaveTask task = new SaveTask(getContext());
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNetworkAvailable == null) {
            isNetworkAvailable = Utility.isNetworkAvailable(getContext());
            DataLensesTask task = new DataLensesTask();
            task.execute();
        } else {
            if (isNetworkAvailable == Boolean.FALSE && Utility.isNetworkAvailable(getContext())) {
                SyncDataLensesTask task = new SyncDataLensesTask(getContext());
                task.execute();
            } else {
                DataLensesTask task = new DataLensesTask();
                task.execute();
            }
            isNetworkAvailable = Utility.isNetworkAvailable(getContext());
        }

//        DataLensesTask task = new DataLensesTask();

//        DataLensesTask task = new DataLensesTask(getContext(), progressDlg,
//                dataLensesCollectionPagerAdapter,
//                mViewPager, this, getFragmentManager(), view);
//        task.execute();

        mTracker.setScreenName("DataLensesFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isSaveVisible = false;
//        DataLensesDAO.getInstance(getContext()).getLastDataLensesAsync();
    }

    public DataLensesVO setDataLensesVO() {
        DataLensesVO vo = null;
        if (getViewsFragmentLenses()) {

            vo = new DataLensesVO();

		/* Left Lens */
            vo.setId(textViewIdDataLens.getText().toString());
            vo.setObjectId(textViewObjectIdDataLens.getText().toString());
            vo.setBrandLeft(editTextBrandLeft.getText().toString());
            vo.setDescriptionLeft(editTextDescLeft.getText().toString());
            vo.setBuySiteLeft(editTextBuySiteLeft.getText().toString());
            vo.setTypeLeft(spinnerTypeLensLeft.getSelectedItemPosition());

            String bcTTxt = editTextBCLeft.getText().toString();
            Double bc = !"".equals(bcTTxt) ? Double.valueOf(bcTTxt) : null;
            String diaTTxt = editTextDiaLeft.getText().toString();
            Double dia = !"".equals(diaTTxt) ? Double.valueOf(diaTTxt) : null;

            vo.setBcLeft(bc);
            vo.setDiaLeft(dia);

            // Myopia/Hypermetropia
            if (spinnerTypeLensLeft.getSelectedItemPosition() == 0) {
                vo.setPowerLeft(spinnerPowerLeft.getSelectedItemPosition());
                vo.setAddLeft(null);
                vo.setAxisLeft(null);
                vo.setCylinderLeft(null);
                // Astigmatism
            } else if (spinnerTypeLensLeft.getSelectedItemPosition() == 1) {
                vo.setPowerLeft(spinnerPowerLeft.getSelectedItemPosition());
                vo.setCylinderLeft(spinnerCylinderLeft.getSelectedItemPosition());
                vo.setAxisLeft(spinnerAxisLeft.getSelectedItemPosition());
                vo.setAddLeft(null);
                // Multifocal/Presbyopia
            } else if (spinnerTypeLensLeft.getSelectedItemPosition() == 2) {
                vo.setPowerLeft(spinnerPowerLeft.getSelectedItemPosition());
                vo.setAddLeft(spinnerAddLeft.getSelectedItemPosition());
                vo.setAxisLeft(null);
                vo.setCylinderLeft(null);
                // Colored/No degree
            } else if (spinnerTypeLensLeft.getSelectedItemPosition() == 3) {
                vo.setPowerLeft(null);
                vo.setAddLeft(null);
                vo.setAxisLeft(null);
                vo.setCylinderLeft(null);
            }

		/* Right Lens */
            vo.setBrandRight(editTextBrandRight.getText().toString());
            vo.setDescriptionRight(editTextDescRight.getText().toString());
            vo.setBuySiteRight(editTextBuySiteRight.getText().toString());
            vo.setTypeRight(spinnerTypeLensRight.getSelectedItemPosition());

            String bcTTxtRight = editTextBCRight.getText().toString();
            Double bcRight = !"".equals(bcTTxtRight) ? Double.valueOf(bcTTxtRight) : null;
            String diaTTxtRight = editTextDiaRight.getText().toString();
            Double diaRight = !"".equals(diaTTxtRight) ? Double.valueOf(diaTTxtRight) : null;

            vo.setBcRight(bcRight);
            vo.setDiaRight(diaRight);

            // Myopia/Hypermetropia
            if (spinnerTypeLensRight.getSelectedItemPosition() == 0) {
                vo.setPowerRight(spinnerPowerRight.getSelectedItemPosition());
                vo.setAddRight(null);
                vo.setAxisRight(null);
                vo.setCylinderRight(null);
                // Astigmatism
            } else if (spinnerTypeLensRight.getSelectedItemPosition() == 1) {
                vo.setPowerRight(spinnerPowerRight.getSelectedItemPosition());
                vo.setCylinderRight(spinnerCylinderRight.getSelectedItemPosition());
                vo.setAxisRight(spinnerAxisRight.getSelectedItemPosition());
                vo.setAddRight(null);
                // Multifocal/Presbyopia
            } else if (spinnerTypeLensRight.getSelectedItemPosition() == 2) {
                vo.setPowerRight(spinnerPowerRight.getSelectedItemPosition());
                vo.setAddRight(spinnerAddRight.getSelectedItemPosition());
                vo.setAxisRight(null);
                vo.setCylinderRight(null);
                // Colored/No degree
            } else if (spinnerTypeLensRight.getSelectedItemPosition() == 3) {
                vo.setPowerRight(null);
                vo.setAddRight(null);
                vo.setAxisRight(null);
                vo.setCylinderRight(null);
            }

        }
        return vo;
    }

    private class SyncDataLensesTask extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public SyncDataLensesTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return DataLensesDAO.getInstance(getContext()).syncDataLenses();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlgSync = new ProgressDialog(context);
            progressDlgSync.setMessage(getResources().getString(R.string.sync));
            progressDlgSync.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlgSync.setIndeterminate(true);
            progressDlgSync.show();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (progressDlgSync != null && progressDlgSync.isShowing())
                progressDlgSync.dismiss();

            DataLensesTask task = new DataLensesTask();
            task.execute();
        }
    }

    public class DataLensesTask extends AsyncTask<String, Void, DataLensesVO> {
        public DataLensesTask() {
        }

        @Override
        protected DataLensesVO doInBackground(String... params) {
            return DataLensesDAO.getInstance(getContext()).getLastDataLenses();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = new ProgressDialog(getContext());
            progressDlg.setMessage(getResources().getString(R.string.loading));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }

        @Override
        protected void onPostExecute(DataLensesVO vo) {
//            dataLensesVO = vo;
            dataLensesCollectionPagerAdapter
                    = new DataLensesCollectionPagerAdapter(getFragmentManager(), getContext(), vo);

            mViewPager = (ViewPager) view.findViewById(R.id.pagerDataLenses);
            mViewPager.setAdapter(dataLensesCollectionPagerAdapter);
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            mShareActionProvider.setShareIntent(getDefaultIntent(vo));
        }
    }


    private class SaveTask extends AsyncTask<String, Void, DataLensesVO> {
        private Context context;

        public SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected DataLensesVO doInBackground(String... params) {
            DataLensesDAO dataLensesDAO = DataLensesDAO.getInstance(getContext());

            DataLensesVO vo = setDataLensesVO();
//            dataLensesVO = vo;

            if (vo.getId() == null || "".equals(vo.getId())) {
                vo.setId(UUID.randomUUID().toString());
                dataLensesDAO.insert(vo);
            } else {
                dataLensesDAO.update(vo);
            }
            return vo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(getResources().getString(R.string.saving));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }

        @Override
        protected void onPostExecute(DataLensesVO vo) {
            super.onPostExecute(vo);

            mShareActionProvider.setShareIntent(getDefaultIntent(vo));

            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();

            Toast.makeText(context, R.string.msgSaved, Toast.LENGTH_SHORT).show();
        }
    }

}
