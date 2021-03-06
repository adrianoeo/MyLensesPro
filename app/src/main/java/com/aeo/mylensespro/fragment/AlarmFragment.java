package com.aeo.mylensespro.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.AlarmDAO;
import com.aeo.mylensespro.slidetab.SlidingTabLayout;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.vo.AlarmVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AlarmFragment extends Fragment {

    private NumberPicker numberDaysBefore;
    private CheckBox cbRemindEveryDay;
    private static Button btnTimePickerAlarm;

    private static Context context;

    public static int idAlarm;
    private Tracker mTracker;

    private AlarmVO alarmVO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        idAlarm = view.getId();

        btnTimePickerAlarm = (Button) view.findViewById(R.id.btnTimePickerAlarm);

        numberDaysBefore = (NumberPicker) view
                .findViewById(R.id.numberPickerDaysBefore);
        cbRemindEveryDay = (CheckBox) view.findViewById(R.id.cbRemindEveryDay);

        //Retira Tab referente ao Fragment do Periodo das lentes
        View viewMain = getActivity().findViewById(R.id.drawer_layout);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) viewMain.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(null);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();

        btnTimePickerAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time[] = getTime();
                int hourOfDay = time[0];
                int minute = time[1];

                TimePickerFragmentAlarm fragmentTime = TimePickerFragmentAlarm.newInstance(hourOfDay, minute);
                fragmentTime.show(getFragmentManager(), "timePickerAlarm");
            }

        });

        setHasOptionsMenu(true);

        setNumberPicker();
        setTime();

        // Obtain the shared Tracker instance.
        MyLensesApplication application = (MyLensesApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return view;
    }

    private void setNumberPicker() {
        numberDaysBefore.setMinValue(0);
        numberDaysBefore.setMaxValue(30);
        numberDaysBefore.setWrapSelectorWheel(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName("AlarmFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setTime() {
        alarmVO = AlarmDAO.alarmVO;
        if (alarmVO != null) {
            btnTimePickerAlarm.setText(getTimeText(alarmVO.getHour(), alarmVO.getMinute()));
            numberDaysBefore.setValue((int) alarmVO.getDaysBefore());
            cbRemindEveryDay.setChecked(alarmVO.getRemindEveryDay() == 1 ? true : false);
        } else {
            if (is24HourFormat()) {
                btnTimePickerAlarm.setText("12:00");
            } else {
                btnTimePickerAlarm.setText("12:00 AM");
            }
            numberDaysBefore.setValue(0);
            cbRemindEveryDay.setChecked(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    private void save() {
        AlarmVO vo = new AlarmVO();

        int time[] = getTime();

        vo.setHour(time[0]);
        vo.setMinute(time[1]);

        numberDaysBefore.clearFocus();
        vo.setDaysBefore(Integer.valueOf(numberDaysBefore.getValue()));
        vo.setRemindEveryDay(cbRemindEveryDay.isChecked() ? 1 : 0);

        AlarmDAO dao = AlarmDAO.getInstance(getContext());
        AlarmDAO.alarmVO = vo;

        if (alarmVO == null) {
            dao.insert(vo);
        } else {
            dao.update(vo);
        }
    }

    //Dialog TimerPicker
    public static class TimePickerFragmentAlarm extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        private static final String HOUR_OF_DAY = "HOUR_OF_DAY";
        private static final String MINUTE = "MINUTE";

        public static TimePickerFragmentAlarm newInstance(int hourOfDay, int minute) {

            TimePickerFragmentAlarm timePickerFragmentAlarm = new TimePickerFragmentAlarm();
            Bundle args = new Bundle();
            args.putInt(HOUR_OF_DAY, hourOfDay);
            args.putInt(MINUTE, minute);
            timePickerFragmentAlarm.setArguments(args);
            return timePickerFragmentAlarm;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hourOfDay = getArguments().getInt(HOUR_OF_DAY);
            int minute = getArguments().getInt(MINUTE);

            boolean timeFormat24 = DateFormat.is24HourFormat(getContext());

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getContext(), this, hourOfDay, minute, timeFormat24);
        }


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btnTimePickerAlarm.setText(getTimeText(hourOfDay, minute));
        }
    }

    private int[] getTime() {
        String[] time = btnTimePickerAlarm.getText().toString().split(":");

        boolean timeFormat24 = is24HourFormat();

        int hour = Integer.valueOf(time[0]);

        String[] minuteAndAmPm = time[1].split("\\s+");

        int minute = Integer.valueOf(minuteAndAmPm[0]);

        if (!timeFormat24 && minuteAndAmPm.length > 1) {
            String ampm = minuteAndAmPm[1];
            if ("PM".equals(ampm) && hour != 12) {
                hour = 12 + hour;
            }
        }

        return new int[]{hour, minute};
    }

    public static String getTimeText(int hourOfDay, int minute) {
        String text = null;
        if (DateFormat.is24HourFormat(context)) {
            text = String.format("%02d:%02d", hourOfDay, minute);
        } else {
            String ampm = null;
            if (hourOfDay >= 12) {
                ampm = "PM";
            } else {
                ampm = "AM";
            }

            if (hourOfDay > 12) {
                hourOfDay = hourOfDay - 12;
            } else if (hourOfDay == 0) {
                hourOfDay = 12;
            }

            text = String.format("%02d:%02d %s", hourOfDay, minute, ampm);
        }

        return text;
    }

    public boolean is24HourFormat() {
        return DateFormat.is24HourFormat(getContext());
    }
}
