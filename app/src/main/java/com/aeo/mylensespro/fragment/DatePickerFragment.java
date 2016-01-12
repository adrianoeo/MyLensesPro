package com.aeo.mylensespro.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.DatePicker;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.TimeLensesCollectionPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private static final String DAY = "DAY";
    private static final String MONTH = "MONTH";
    private static final String YEAR = "YEAR";
    private static final String TAG = "TAG";

    private int day;
    private int month;
    private int year;

    private Button btnDateLeft;
    private Button btnDateRight;
    private String tag;

    public static DatePickerFragment newInstance(String tagLens, int year,
                                                 int month, int day) {

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tagLens);
        args.putInt(YEAR, year);
        args.putInt(MONTH, month);
        args.putInt(DAY, day);
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = getArguments().getInt(YEAR);
        int month = getArguments().getInt(MONTH);
        int day = getArguments().getInt(DAY);
        tag = getArguments().getString(TAG);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Fragment fragment = null;

        ViewPager mViewPager = (ViewPager) getActivity().findViewById(
                R.id.pagerTimeLenses);
        int index = mViewPager.getCurrentItem();
        TimeLensesCollectionPagerAdapter adapter = ((TimeLensesCollectionPagerAdapter) mViewPager
                .getAdapter());

        // Fragment of lenses/replacement date
        if (tag.equals(LeftTimeFragment.DATE_LEFT_EYE)) {
            fragment = (LeftTimeFragment) adapter.getFragment(index);
        } else if (tag.equals(RightTimeFragment.DATE_RIGHT_EYE)) {
            fragment = (RightTimeFragment) adapter.getFragment(index);
        }

        this.day = day;
        this.month = month + 1;
        this.year = year;

//        String strDate = new StringBuilder(String.format("%02d", day))
//                .append("/").append(String.format("%02d", month + 1))
//                .append("/").append(String.valueOf(year)).toString();

        String dateFormat = getContext().getResources().getString(R.string.locale);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String strDate = new SimpleDateFormat(dateFormat).format(calendar.getTime());

        if (tag.equals(LeftTimeFragment.DATE_LEFT_EYE)) {
            btnDateLeft = (Button) fragment.getView().findViewById(
                    R.id.btnDateLeft);
            btnDateLeft.setText(strDate);
        } else if (tag.equals(RightTimeFragment.DATE_RIGHT_EYE)) {
            btnDateRight = (Button) fragment.getView().findViewById(
                    R.id.btnDateRight);
            btnDateRight.setText(strDate);
        }
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

}
