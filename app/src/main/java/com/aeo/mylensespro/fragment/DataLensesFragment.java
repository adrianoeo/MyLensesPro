package com.aeo.mylensespro.fragment;

import android.content.Intent;
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

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.DataLensesCollectionPagerAdapter;
import com.aeo.mylensespro.dao.LensesDataDAO;
import com.aeo.mylensespro.slidetab.SlidingTabLayout;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.vo.DataLensesVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
    private MenuItem menuItemDelete;
    private MenuItem menuItemCancel;
    private MenuItem menuItemShare;

    private EditText editTextDescLeft;
    private AutoCompleteTextView editTextBrandLeft;
    private EditText editTextBuySiteLeft;
    private Spinner spinnerTypeLensLeft;
    private Spinner spinnerPowerLeft;
    private Spinner spinnerAddLeft;
    private Spinner spinnerAxisLeft;
    private Spinner spinnerCylinderLeft;
    private EditText editTextBCLeft;
    private EditText editTextDiaLeft;

    private EditText editTextDescRight;
    private AutoCompleteTextView editTextBrandRight;
    private EditText editTextBuySiteRight;
    private Spinner spinnerTypeLensRight;
    private Spinner spinnerPowerRight;
    private Spinner spinnerAddRight;
    private Spinner spinnerAxisRight;
    private Spinner spinnerCylinderRight;
    private EditText editTextBCRight;
    private EditText editTextDiaRight;

    private LinearLayout layoutLeft;
    private LinearLayout layoutRight;
    private ShareActionProvider mShareActionProvider;

    private static boolean isSaveVisible;
    private Tracker mTracker;

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
        View view = inflater.inflate(R.layout.fragment_data_lenses, container, false);

        dataLensesCollectionPagerAdapter
                = new DataLensesCollectionPagerAdapter(getFragmentManager(), getContext());

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
        mShareActionProvider.setShareIntent(getDefaultIntent());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment leftFragment = dataLensesCollectionPagerAdapter.getFragment(0);
        Fragment rightFragment = dataLensesCollectionPagerAdapter.getFragment(1);

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
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.str_email_subject_data_lenses));
        intent.putExtra(Intent.EXTRA_TEXT, getDataLenses());

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());

        return intent;
    }

    private String getDataLenses() {
        LensesDataDAO dao = LensesDataDAO.getInstance(getContext());
        DataLensesVO vo = dao.getById(dao.getLastIdLens());

        StringBuilder text = new StringBuilder();

        if (vo != null) {
            text.append("\n");
            text.append("- ").append(getString(R.string.tabLeftLens))
                    .append("\n\n");
            text.append(getLeftText(vo)).append("\n\n");
            text.append("- ").append(getString(R.string.tabRightLens))
                    .append("\n\n");
            text.append(getRightText(vo)).append("\n");
        }

        return text.toString();
    }

    private String getLeftText(DataLensesVO vo) {
        String typeLensLeft = vo.getType_left() == null
                || "".equals(vo.getType_left()) ? "" : getResources()
                .getStringArray(R.array.array_type_lens)[Integer.valueOf(vo
                .getType_left())];

        String powerLeft = vo.getPower_left() == null
                || "".equals(vo.getPower_left()) ? "" : getResources()
                .getStringArray(R.array.array_power)[Integer.valueOf(vo
                .getPower_left())];

        String addLeft = vo.getAdd_left() == null
                || "".equals(vo.getAdd_left()) ? "" : getResources()
                .getStringArray(R.array.array_add)[Integer.valueOf(vo
                .getAdd_left())];

        String axisLeft = vo.getAxis_left() == null
                || "".equals(vo.getAxis_left()) ? "" : getResources()
                .getStringArray(R.array.array_axis)[Integer.valueOf(vo
                .getAxis_left())];

        String cylinderLeft = vo.getCylinder_left() == null
                || "".equals(vo.getCylinder_left()) ? "" : getResources()
                .getStringArray(R.array.array_cylinder)[Integer.valueOf(vo
                .getCylinder_left())];

        StringBuilder sbLeft = new StringBuilder();
        sbLeft.append(getString(R.string.lbl_brand)).append(": ")
                .append(vo.getBrand_left() == null ? "" : vo.getBrand_left())
                .append("\n");
        sbLeft.append(getString(R.string.lbl_desc_lens))
                .append(": ")
                .append(vo.getDescription_left() == null ? "" : vo
                        .getDescription_left()).append("\n");
        sbLeft.append(getString(R.string.lbl_type_lens)).append(": ")
                .append(typeLensLeft).append("\n");

        // Miophya/Hipermetrophya
        if (vo.getType_left() != null) {
            if ("0".equals(vo.getType_left().toString())) {
                sbLeft.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerLeft).append("\n");
                // Astigmatism
            } else if ("1".equals(vo.getType_left().toString())) {
                sbLeft.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerLeft).append("\n");
                sbLeft.append(getString(R.string.lbl_cylinder)).append(": ")
                        .append(cylinderLeft).append("\n");
                sbLeft.append(getString(R.string.lbl_axis)).append(": ")
                        .append(axisLeft).append("\n");
                // Multifocal/Presbyopia
            } else if ("2".equals(vo.getType_left().toString())) {
                sbLeft.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerLeft).append("\n");
                sbLeft.append(getString(R.string.lbl_add)).append(": ")
                        .append(addLeft).append("\n");
            }
        }
        sbLeft.append(getString(R.string.lbl_bc)).append(": ")
                .append(vo.getBc_left() == null ? "" : vo.getBc_left())
                .append("\n");
        sbLeft.append(getString(R.string.lbl_dia)).append(": ")
                .append(vo.getDia_left() == null ? "" : vo.getDia_left())
                .append("\n");
        return sbLeft.toString();
    }

    private String getRightText(DataLensesVO vo) {
        String typeLensRight = vo.getType_right() == null
                || "".equals(vo.getType_right()) ? "" : getResources()
                .getStringArray(R.array.array_type_lens)[Integer.valueOf(vo
                .getType_right())];

        String powerRight = vo.getPower_right() == null
                || "".equals(vo.getPower_right()) ? "" : getResources()
                .getStringArray(R.array.array_power)[Integer.valueOf(vo
                .getPower_right())];

        String addRight = vo.getAdd_right() == null
                || "".equals(vo.getAdd_right()) ? "" : getResources()
                .getStringArray(R.array.array_add)[Integer.valueOf(vo
                .getAdd_right())];

        String axisRight = vo.getAxis_right() == null
                || "".equals(vo.getAxis_right()) ? "" : getResources()
                .getStringArray(R.array.array_axis)[Integer.valueOf(vo
                .getAxis_right())];

        String cylinderRight = vo.getCylinder_right() == null
                || "".equals(vo.getCylinder_right()) ? "" : getResources()
                .getStringArray(R.array.array_cylinder)[Integer.valueOf(vo
                .getCylinder_right())];

        StringBuilder sbRight = new StringBuilder();
        sbRight.append(getString(R.string.lbl_brand)).append(": ")
                .append(vo.getBrand_right() == null ? "" : vo.getBrand_right())
                .append("\n");
        sbRight.append(getString(R.string.lbl_desc_lens))
                .append(": ")
                .append(vo.getDescription_right() == null ? "" : vo
                        .getDescription_right()).append("\n");
        sbRight.append(getString(R.string.lbl_type_lens)).append(": ")
                .append(typeLensRight).append("\n");

        // Miophya/Hipermetrophya
        if (vo.getType_right() != null) {
            if ("0".equals(vo.getType_right().toString())) {
                sbRight.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerRight).append("\n");
                // Astigmatism
            } else if ("1".equals(vo.getType_right().toString())) {
                sbRight.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerRight).append("\n");
                sbRight.append(getString(R.string.lbl_cylinder)).append(": ")
                        .append(cylinderRight).append("\n");
                sbRight.append(getString(R.string.lbl_axis)).append(": ")
                        .append(axisRight).append("\n");
                // Multifocal/Presbyopia
            } else if ("2".equals(vo.getType_right().toString())) {
                sbRight.append(getString(R.string.lbl_power)).append(": ")
                        .append(powerRight).append("\n");
                sbRight.append(getString(R.string.lbl_add)).append(": ")
                        .append(addRight).append("\n");
            }
        }
        sbRight.append(getString(R.string.lbl_bc)).append(": ")
                .append(vo.getBc_right() == null ? "" : vo.getBc_right())
                .append("\n");
        sbRight.append(getString(R.string.lbl_dia)).append(": ")
                .append(vo.getDia_right() == null ? "" : vo.getDia_right())
                .append("\n");

        return sbRight.toString();
    }

    private void save() {
        if (getViewsFragmentLenses()) {

            DataLensesVO vo = new DataLensesVO();

		/* Left Lens */
            vo.setBrand_left(editTextBrandLeft.getText().toString());
            vo.setDescription_left(editTextDescLeft.getText().toString());
            vo.setBuy_site_left(editTextBuySiteLeft.getText().toString());
            vo.setType_left(String.valueOf(spinnerTypeLensLeft
                    .getSelectedItemPosition()));

            String bcTTxt = editTextBCLeft.getText().toString();
            Double bc = !"".equals(bcTTxt) ? Double.valueOf(bcTTxt) : null;
            String diaTTxt = editTextDiaLeft.getText().toString();
            Double dia = !"".equals(diaTTxt) ? Double.valueOf(diaTTxt) : null;

            vo.setBc_left(bc);
            vo.setDia_left(dia);

            // Myopia/Hypermetropia
            if (spinnerTypeLensLeft.getSelectedItemPosition() == 0) {
                vo.setPower_left(String.valueOf(spinnerPowerLeft
                        .getSelectedItemPosition()));
                vo.setAdd_left(null);
                vo.setAxis_left(null);
                vo.setCylinder_left(null);
                // Astigmatism
            } else if (spinnerTypeLensLeft.getSelectedItemPosition() == 1) {
                vo.setPower_left(String.valueOf(spinnerPowerLeft
                        .getSelectedItemPosition()));
                vo.setCylinder_left(String.valueOf(spinnerCylinderLeft
                        .getSelectedItemPosition()));
                vo.setAxis_left(String.valueOf(spinnerAxisLeft
                        .getSelectedItemPosition()));
                vo.setAdd_left(null);
                // Multifocal/Presbyopia
            } else if (spinnerTypeLensLeft.getSelectedItemPosition() == 2) {
                vo.setPower_left(String.valueOf(spinnerPowerLeft
                        .getSelectedItemPosition()));
                vo.setAdd_left(String.valueOf(spinnerAddLeft
                        .getSelectedItemPosition()));
                vo.setAxis_left(null);
                vo.setCylinder_left(null);
                // Colored/No degree
            } else if (spinnerTypeLensLeft.getSelectedItemPosition() == 3) {
                vo.setPower_left(null);
                vo.setAdd_left(null);
                vo.setAxis_left(null);
                vo.setCylinder_left(null);
            }

		/* Right Lens */
            vo.setBrand_right(editTextBrandRight.getText().toString());
            vo.setDescription_right(editTextDescRight.getText().toString());
            vo.setBuy_site_right(editTextBuySiteRight.getText().toString());
            vo.setType_right(String.valueOf(spinnerTypeLensRight
                    .getSelectedItemPosition()));

            String bcTTxtRight = editTextBCRight.getText().toString();
            Double bcRight = !"".equals(bcTTxtRight) ? Double.valueOf(bcTTxtRight) : null;
            String diaTTxtRight = editTextDiaRight.getText().toString();
            Double diaRight = !"".equals(diaTTxtRight) ? Double.valueOf(diaTTxtRight) : null;

            vo.setBc_right(bcRight);
            vo.setDia_right(diaRight);

            // Myopia/Hypermetropia
            if (spinnerTypeLensRight.getSelectedItemPosition() == 0) {
                vo.setPower_right(String.valueOf(spinnerPowerRight
                        .getSelectedItemPosition()));
                vo.setAdd_right(null);
                vo.setAxis_right(null);
                vo.setCylinder_right(null);
                // Astigmatism
            } else if (spinnerTypeLensRight.getSelectedItemPosition() == 1) {
                vo.setPower_right(String.valueOf(spinnerPowerRight
                        .getSelectedItemPosition()));
                vo.setCylinder_right(String.valueOf(spinnerCylinderRight
                        .getSelectedItemPosition()));
                vo.setAxis_right(String.valueOf(spinnerAxisRight
                        .getSelectedItemPosition()));
                vo.setAdd_right(null);
                // Multifocal/Presbyopia
            } else if (spinnerTypeLensRight.getSelectedItemPosition() == 2) {
                vo.setPower_right(String.valueOf(spinnerPowerRight
                        .getSelectedItemPosition()));
                vo.setAdd_right(String.valueOf(spinnerAddRight
                        .getSelectedItemPosition()));
                vo.setAxis_right(null);
                vo.setCylinder_right(null);
                // Colored/No degree
            } else if (spinnerTypeLensRight.getSelectedItemPosition() == 3) {
                vo.setPower_right(null);
                vo.setAdd_right(null);
                vo.setAxis_right(null);
                vo.setCylinder_right(null);
            }

            LensesDataDAO lensesDataDAO = LensesDataDAO.getInstance(getContext());

            int idLastItemDataLenses = lensesDataDAO.getLastIdLens();

            // If there are not lenses data then insert. If there are lenses
            // replacement then insert history.
            if (idLastItemDataLenses == 0) {
                lensesDataDAO.insert(vo);
                // If there are lenses data then update. If there are lenses
                // replacement then update history.
            } else {
                vo.setId(idLastItemDataLenses);
                lensesDataDAO.update(vo);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName("DataLensesFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isSaveVisible = false;
    }
}
