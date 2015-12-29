package com.aeo.mylensespro.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.AlarmDAO;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.slidetab.SlidingTabLayout;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.vo.AlarmVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AlarmFragment extends Fragment {
    //    private TimePicker timePicker;
    private NumberPicker numberDaysBefore;
    private CheckBox cbRemindEveryDay;
    private static Button btnTimePickerAlarm;

    private Context context;

    public static int idAlarm;
    private Tracker mTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        idAlarm = view.getId();

//        timePicker = (TimePicker) view.findViewById(R.id.timePickerAlarm);
//        timePicker.setIs24HourView(true);

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
                int hourOfDay = 12;
                int minute = 0;

                String[] time = btnTimePickerAlarm.getText().toString().split(":");

                hourOfDay = Integer.valueOf(time[0]);
                minute = Integer.valueOf(time[1]);

                TimePickerFragmentAlarm fragmentTime = TimePickerFragmentAlarm.newInstance(hourOfDay, minute);
                fragmentTime.show(getFragmentManager(), "timePickerAlarm");
            }

        });

        setHasOptionsMenu(true);

        setNumberPicker();
        setTimeAlarm();

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
//        save();

        mTracker.setScreenName("AlarmFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setTimeAlarm() {
        AlarmDAO dao = AlarmDAO.getInstance(getContext());
        AlarmVO vo = dao.getAlarm();
        if (vo == null) {
//            timePicker.setCurrentHour(0);
//            timePicker.setCurrentMinute(0);
            btnTimePickerAlarm.setText("12:00");
            numberDaysBefore.setValue(0);
            cbRemindEveryDay.setChecked(true);
        } else {
//            timePicker.setCurrentHour(vo.getHour());
//            timePicker.setCurrentMinute(vo.getMinute());
            btnTimePickerAlarm.setText(String.format("%02d:%02d", vo.getHour(), vo.getMinute()));
            numberDaysBefore.setValue((int) vo.getDaysBefore());
            cbRemindEveryDay.setChecked(vo.getRemindEveryDay() == 1 ? true : false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        save();
    }

    private void save() {
        AlarmVO vo = new AlarmVO();
//        vo.setHour(timePicker.getCurrentHour());
//        vo.setMinute(timePicker.getCurrentMinute());

        String[] time = btnTimePickerAlarm.getText().toString().split(":");

        vo.setHour(Long.valueOf(time[0]));
        vo.setMinute(Long.valueOf(time[1]));
        vo.setDaysBefore(Long.valueOf(numberDaysBefore.getValue()));
        vo.setRemindEveryDay(cbRemindEveryDay.isChecked() ? 1L : 0L);

        AlarmDAO dao = AlarmDAO.getInstance(getContext());
        if (dao.getAlarm() == null) {
            dao.insert(vo);
        } else {
            dao.update(vo);
        }

        int idLenses = TimeLensesDAO.getInstance(context).getLastIdLens();
        if (idLenses != 0) {
            dao.setAlarm(idLenses);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItemInsert = menu.findItem(R.id.menuInsertLens);
        menuItemInsert.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
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

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getContext(), this, hourOfDay, minute, true);
        }


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btnTimePickerAlarm.setText(String.format("%02d:%02d", hourOfDay, minute));
        }
    }
}
