package com.aeo.mylensespro.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.TimeLensesCollectionPagerAdapter;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.slidetab.SlidingTabLayout;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.vo.TimeLensesVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link TimeLensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeLensesFragment extends Fragment {
    TimeLensesCollectionPagerAdapter timeLensesCollectionPagerAdapter;
    ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private MenuItem menuItemEdit;
    private MenuItem menuItemSave;
    private MenuItem menuItemCancel;
    private MenuItem menuItemDelete;

    private static Button btnDateLeft;
    private static NumberPicker numberPickerLeft;
    private static CheckBox cbInUseLeft;
    private static NumberPicker qtdLeft;
    private static Spinner spinnerLeft;
    private static Button btnCopyToRight;

    private static Button btnDateRight;
    private static NumberPicker numberPickerRight;
    private static CheckBox cbInUseRight;
    private static NumberPicker qtdRight;
    private static Spinner spinnerRight;

    public static final String KEY_ID_LENS = "KEY_ID_LENS";

    public static boolean isSaveVisible;
    private Tracker mTracker;

    private ProgressDialog progressDlg;

    private static TimeLensesVO timeLensesVO;

    public TimeLensesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vo timeLensesVO.
     * @return A new instance of fragment TimeLensesFragment.
     */
    public static TimeLensesFragment newInstance(TimeLensesVO vo, boolean isSaveVisible1) {
        isSaveVisible = isSaveVisible1;
        timeLensesVO = vo;
        TimeLensesFragment fragment = new TimeLensesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("TimeLensesFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_lenses, container, false);

        timeLensesCollectionPagerAdapter
                = new TimeLensesCollectionPagerAdapter(getFragmentManager(), getContext(), timeLensesVO);

        mViewPager = (ViewPager) view.findViewById(R.id.pagerTimeLenses);
        mViewPager.setAdapter(timeLensesCollectionPagerAdapter);

        View viewMain = getActivity().findViewById(R.id.drawer_layout);

        mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);
        mSlidingTabLayout.setViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        Toolbar toolbar = (Toolbar) viewMain.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.title_periodo);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuItemSave != null && menuItemSave.isVisible()) {
                    saveLens();
                }
                returnToPreviousFragment();
            }
        });

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
        getActivity().getMenuInflater().inflate(R.menu.time_lenses, menu);
        menuItemEdit = menu.findItem(R.id.menuEditLenses);
        menuItemSave = menu.findItem(R.id.menuSaveLenses);
        menuItemCancel = menu.findItem(R.id.menuCancelLenses);
        menuItemDelete = menu.findItem(R.id.menuDeleteLenses);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (timeLensesVO == null) {
            enableMenuEdit(false);
            enableMenuSaveCancel(true);
        } else {
            enableMenuSaveCancel(isSaveVisible);
            enableMenuEdit(timeLensesVO.getId() != null
                    && TimeLensesDAO.getInstance(getContext()).getLastIdLens().equals(timeLensesVO.getId())
                    && !isSaveVisible);
        }
    }


    private void enableMenuEdit(boolean enabled) {
        if (menuItemEdit != null) {
            menuItemEdit.setVisible(enabled);
        }
        if (menuItemDelete != null) {
            menuItemDelete.setVisible(enabled);
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

    private void enableControls(boolean enabled) {
        if (getViewsFragmentLenses()) {
            btnDateLeft.setEnabled(enabled);
            btnCopyToRight.setEnabled(enabled);
            btnDateRight.setEnabled(enabled);
            numberPickerLeft.setEnabled(enabled);
            numberPickerRight.setEnabled(enabled);
            spinnerLeft.setEnabled(enabled);
            spinnerRight.setEnabled(enabled);
            cbInUseLeft.setEnabled(enabled);
            cbInUseRight.setEnabled(enabled);
            qtdLeft.setEnabled(enabled);
            qtdRight.setEnabled(enabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnToPreviousFragment();
                return true;
            case R.id.menuSaveLenses:
                saveLens();
                return true;
            case R.id.menuEditLenses:
                enableControls(true);
                item.setVisible(false);
                enableMenuEdit(false);
                enableMenuSaveCancel(true);
                isSaveVisible = true;
                return true;
            case R.id.menuCancelLenses:
                returnToPreviousFragment();
                return true;
            case R.id.menuDeleteLenses:
                deleteLens(timeLensesVO.getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveLens() {
        if (getViewsFragmentLenses()) {
            SaveTask task = new SaveTask(getContext(), setTimeLensesVO());
            task.execute();
        }
    }

    private void deleteLens(final String id) {
        DeleteTask task = new DeleteTask(getContext());
        task.execute(new String[]{id});
    }

    private boolean getViewsFragmentLenses() {
        Fragment leftFragment = timeLensesCollectionPagerAdapter.getFragment(0);

        if (leftFragment != null) {
            View leftView = leftFragment.getView();

            if (leftView != null) {
                spinnerLeft = (Spinner) leftView.findViewById(R.id.spinnerLeft);
                numberPickerLeft = (NumberPicker) leftView
                        .findViewById(R.id.numberPickerLeft);
                btnDateLeft = (Button) leftView.findViewById(R.id.btnDateLeft);
                cbInUseLeft = (CheckBox) leftView.findViewById(R.id.cbxWearLeft);
                qtdLeft = (NumberPicker) leftView.findViewById(R.id.qtdLeft);
                btnCopyToRight = (Button) leftView.findViewById(R.id.btnCopyToRight);
            }
        }

        Fragment rightFragment = timeLensesCollectionPagerAdapter.getFragment(1);

        if (rightFragment != null) {
            View rightView = rightFragment.getView();

            if (rightView != null) {
                spinnerRight = (Spinner) rightView.findViewById(R.id.spinnerRight);
                numberPickerRight = (NumberPicker) rightView
                        .findViewById(R.id.numberPickerRight);
                btnDateRight = (Button) rightView.findViewById(R.id.btnDateRight);
                cbInUseRight = (CheckBox) rightView.findViewById(R.id.cbxWearRight);
                qtdRight = (NumberPicker) rightView.findViewById(R.id.qtdRight);
            }
        }

        return leftFragment != null && rightFragment != null;
    }

    private TimeLensesVO setTimeLensesVO() {
        TimeLensesVO lensesVO = new TimeLensesVO();

        // Quando número é digitado manualmente através do teclado, é necessário dar clearFocus()
        // para atualizar a view com o número digitado, caso contrário, o valor qdo obtido
        // por getValue() retornará o número anterior.
        numberPickerLeft.clearFocus();
        numberPickerRight.clearFocus();

        lensesVO.setDateLeft(btnDateLeft.getText().toString());
        lensesVO.setDateRight(btnDateRight.getText().toString());
        lensesVO.setExpirationLeft(numberPickerLeft.getValue());
        lensesVO.setExpirationRight(numberPickerRight.getValue());
        lensesVO.setTypeLeft(spinnerLeft.getSelectedItemPosition());
        lensesVO.setTypeRight(spinnerRight.getSelectedItemPosition());
        lensesVO.setInUseLeft(cbInUseLeft.isChecked() ? 1 : 0);
        lensesVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
        lensesVO.setQtdLeft(qtdLeft.getValue());
        lensesVO.setQtdRight(qtdRight.getValue());

        if (timeLensesVO != null) {
            lensesVO.setId(timeLensesVO.getId());
            lensesVO.setObjectId(timeLensesVO.getObjectId());
        }

        return lensesVO;
    }

    private void returnToPreviousFragment() {
        if (getFragmentManager() != null) {
            isSaveVisible = false;
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDlg != null && progressDlg.isShowing())
            progressDlg.dismiss();

        progressDlg = null;
    }

    private class SaveTask extends AsyncTask<String, Void, Boolean> {
        private Context context;
        private TimeLensesVO timeLensesVO;

        public SaveTask(Context context, TimeLensesVO timeLensesVO) {
            this.context = context;
            this.timeLensesVO = timeLensesVO;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
            timeLensesDAO.save(timeLensesVO);
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();
            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(getResources().getString(R.string.saving));
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDlg.setIndeterminate(true);
            progressDlg.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();

            Toast.makeText(context, R.string.msgSaved, Toast.LENGTH_SHORT).show();
            returnToPreviousFragment();
        }
    }

    private class DeleteTask extends AsyncTask<String, Void, Void> {
        private Context context;

        public DeleteTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
            timeLensesDAO.delete(params[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDlg != null && progressDlg.isShowing())
                progressDlg.dismiss();

            returnToPreviousFragment();
        }
    }
}
