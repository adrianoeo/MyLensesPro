package com.aeo.mylensespro.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.vo.TimeLensesVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.aeo.mylensespro.dao.AlarmDAO;

@SuppressLint("SimpleDateFormat")
public class RightTimeFragment extends DialogFragment {
    private static Button btnDateRight;
    private DatePickerFragment fragmentDate;
    private static NumberPicker numberPickerRight;
    private static CheckBox cbInUseRight;
    private static NumberPicker qtdRight;
    private static Spinner spinnerRight;

    public static final String DATE_RIGHT_EYE = "DATE_RIGHT_EYE";
    public static final String KEY_ID_LENS = "KEY_ID_LENS";

    private View view;
    private MenuItem menuItemEdit;
    private MenuItem menuItemSave;
    private MenuItem menuItemCancel;
    private MenuItem menuItemDelete;
    public static int idLenses;

    private Context context;

    public static TimeLensesVO lensVO;

    public static RightTimeFragment newInstance(int idLens) {
        RightTimeFragment lensFragment = new RightTimeFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID_LENS, idLens);
        lensFragment.setArguments(args);
        return lensFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS)
                : 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS)
                : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_right_time, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        context = getContext();

        spinnerRight = (Spinner) view.findViewById(R.id.spinnerRight);
        numberPickerRight = (NumberPicker) view
                .findViewById(R.id.numberPickerRight);
        btnDateRight = (Button) view.findViewById(R.id.btnDateRight);
        cbInUseRight = (CheckBox) view.findViewById(R.id.cbxWearRight);
        qtdRight = (NumberPicker) view.findViewById(R.id.qtdRight);

        btnDateRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                try {
                    date.setTime(new SimpleDateFormat("dd/MM/yyyy")
                            .parse(btnDateRight.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                fragmentDate = DatePickerFragment.newInstance(/*
                                                             * RightTimeFragment.this,
															 */DATE_RIGHT_EYE,
                        date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH));
                fragmentDate.show(getFragmentManager(), "datePickerRight");
            }
        });

        setHasOptionsMenu(true);

        setNumberPicker();
        setSpinnerDiscard();
        setLensValues();

        enableControls(menuItemEdit != null && menuItemEdit.isVisible());

        return view;
    }

    private void setSpinnerDiscard() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.discard_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRight.setAdapter(adapter);

        spinnerRight.setSelection(1);
    }

    private void setNumberPicker() {
        numberPickerRight.setMinValue(1);
        numberPickerRight.setMaxValue(100);
        numberPickerRight.setWrapSelectorWheel(false);
        qtdRight.setMinValue(0);
        qtdRight.setMaxValue(30);
        qtdRight.setWrapSelectorWheel(false);
    }

    private void setDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String strDate = new StringBuilder(String.format("%02d", day))
                .append("/").append(String.format("%02d", month + 1))
                .append("/").append(String.valueOf(year)).toString();

        btnDateRight.setText(strDate);
    }

    private void setLensValues() {
        TimeLensesDAO dao = TimeLensesDAO.getInstance(context);
        lensVO = dao.getById(idLenses);
        if (lensVO != null) {
            btnDateRight.setText(lensVO.getDateRight());
            numberPickerRight.setValue(lensVO.getExpirationRight());
            spinnerRight.setSelection(lensVO.getTypeRight());
            cbInUseRight.setChecked(lensVO.getInUseRight() == 1 ? true : false);
            qtdRight.setValue(lensVO.getQtdRight());
        } else {
            setDate();
            cbInUseRight.setChecked(true);

            //Seta qtd
            TimeLensesVO lastLenses = dao.getLastLens();
            if (lastLenses != null) {
                int qtd = lastLenses.getQtdRight() - 1;
                qtdRight.setValue(qtd >= 0 ? qtd : 0);
            }
        }
    }

    private void enableControls(boolean enabled) {
        btnDateRight.setEnabled(enabled);
        numberPickerRight.setEnabled(enabled);
        spinnerRight.setEnabled(enabled);
        cbInUseRight.setEnabled(enabled);
        qtdRight.setEnabled(enabled);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuItemEdit = menu.findItem(R.id.menuEditLenses);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (idLenses == 0) {
            enableControls(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && btnDateRight != null && btnDateRight.isEnabled()) {
//			saveLens();
//			getActivity().finish();
        }
    }

}
