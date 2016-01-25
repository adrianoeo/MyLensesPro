package com.aeo.mylensespro.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.vo.DataLensesVO;

public class RightDataFragment extends Fragment {

	private LinearLayout layout;
	private LinearLayout layoutPower;
	private LinearLayout layoutCylinder;
	private LinearLayout layoutAxis;
	private LinearLayout layoutAdd;

	private AutoCompleteTextView editTextBrand;
	private EditText editTextDesc;
	private EditText editTextBuySite;
	private Spinner spinnerTypeLens;
	private Spinner spinnerPower;
	private Spinner spinnerAdd;
	private Spinner spinnerAxis;
	private Spinner spinnerCylinder;
	private EditText editTextBC;
	private EditText editTextDia;

	private Context context;

	private View view;

	private static DataLensesVO dataLensesVO;

	public static RightDataFragment newInstance(DataLensesVO vo) {
		RightDataFragment dataLensesFragment = new RightDataFragment();
		dataLensesVO = vo;
		return dataLensesFragment;
	}

	public RightDataFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		context = getContext();

		view = inflater.inflate(R.layout.fragment_right_data, container,
				false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		layout = (LinearLayout) view.findViewById(R.id.id_layout_Lens_right);

		editTextDesc = (EditText) view.findViewById(R.id.EditTextDescRight);
		editTextBrand = (AutoCompleteTextView) view
				.findViewById(R.id.editTextRightBrand);
		editTextBuySite = (EditText) view
				.findViewById(R.id.EditTextBuySiteRight);
		editTextBC = (EditText) view.findViewById(R.id.editTextBCRight);
		editTextDia = (EditText) view.findViewById(R.id.editTextDiaRight);

		layoutPower = (LinearLayout) view.findViewById(R.id.layout_power);
		layoutCylinder = (LinearLayout) view.findViewById(R.id.layout_cylinder);
		layoutAxis = (LinearLayout) view.findViewById(R.id.layout_axis);
		layoutAdd = (LinearLayout) view.findViewById(R.id.layout_add);
		layoutPower.setVisibility(View.GONE);
		layoutCylinder.setVisibility(View.GONE);
		layoutAxis.setVisibility(View.GONE);
		layoutAdd.setVisibility(View.GONE);

		spinnerTypeLens = (Spinner) view.findViewById(R.id.spinnerTypeLensRight);
		ArrayAdapter<CharSequence> adapterTypeLens = ArrayAdapter
				.createFromResource(getActivity(), R.array.array_type_lens,
						android.R.layout.simple_spinner_item);
		adapterTypeLens
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTypeLens.setAdapter(adapterTypeLens);

		spinnerPower = (Spinner) view.findViewById(R.id.spinnerPowerRight);
		ArrayAdapter<CharSequence> adapterPower = ArrayAdapter
				.createFromResource(getActivity(), R.array.array_power,
						android.R.layout.simple_spinner_item);
		adapterPower
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPower.setAdapter(adapterPower);

		spinnerCylinder = (Spinner) view.findViewById(R.id.spinnerCylinderRight);
		ArrayAdapter<CharSequence> adapterCylinder = ArrayAdapter
				.createFromResource(getActivity(), R.array.array_cylinder,
						android.R.layout.simple_spinner_item);
		adapterCylinder
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCylinder.setAdapter(adapterCylinder);

		spinnerAxis = (Spinner) view.findViewById(R.id.spinnerAxisRight);
		ArrayAdapter<CharSequence> adapterAxis = ArrayAdapter
				.createFromResource(getActivity(), R.array.array_axis,
						android.R.layout.simple_spinner_item);
		adapterAxis
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAxis.setAdapter(adapterAxis);

		spinnerAdd = (Spinner) view.findViewById(R.id.spinnerAddRight);
		ArrayAdapter<CharSequence> adapterAdd = ArrayAdapter
				.createFromResource(getActivity(), R.array.array_add,
						android.R.layout.simple_spinner_item);
		adapterAdd
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdd.setAdapter(adapterAdd);

		spinnerTypeLens.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int pos, long id) {
				if (parent.getItemIdAtPosition(pos) == 0) {
					layoutPower.setVisibility(View.VISIBLE);
					layoutCylinder.setVisibility(View.GONE);
					layoutAxis.setVisibility(View.GONE);
					layoutAdd.setVisibility(View.GONE);
				} else if (parent.getItemIdAtPosition(pos) == 1) {
					layoutPower.setVisibility(View.VISIBLE);
					layoutCylinder.setVisibility(View.VISIBLE);
					layoutAxis.setVisibility(View.VISIBLE);
					layoutAdd.setVisibility(View.GONE);
				} else if (parent.getItemIdAtPosition(pos) == 2) {
					layoutPower.setVisibility(View.VISIBLE);
					layoutCylinder.setVisibility(View.GONE);
					layoutAxis.setVisibility(View.GONE);
					layoutAdd.setVisibility(View.VISIBLE);
				} else if (parent.getItemIdAtPosition(pos) == 3) {
					layoutPower.setVisibility(View.GONE);
					layoutCylinder.setVisibility(View.GONE);
					layoutAxis.setVisibility(View.GONE);
					layoutAdd.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		ArrayAdapter<CharSequence> adapterBrand = ArrayAdapter
				.createFromResource(getActivity(), R.array.array_brand,
						android.R.layout.simple_list_item_1);
		editTextBrand = (AutoCompleteTextView) view
				.findViewById(R.id.editTextRightBrand);
		editTextBrand.setAdapter(adapterBrand);

		enableControls(false, layout);
		getLens();

		return view;
	}

	private void enableControls(boolean enabled, ViewGroup view) {
		for (int i = 0; i < view.getChildCount(); i++) {
			View child = view.getChildAt(i);
			child.setEnabled(enabled);
			if (child instanceof ViewGroup) {
				enableControls(enabled, (ViewGroup) child);
			}
		}
	}

	private void getLens() {
		if (dataLensesVO != null) {
			editTextDesc.setText(dataLensesVO.getDescriptionRight());
			editTextBrand.setText(dataLensesVO.getBrandRight());
			editTextBuySite.setText(dataLensesVO.getBuySiteRight());
			spinnerPower.setSelection(dataLensesVO.getPowerRight() == null
					|| "".equals(dataLensesVO.getPowerRight()) ? 0 : Integer.valueOf(dataLensesVO
					.getPowerRight()));
			spinnerAdd.setSelection(dataLensesVO.getAddRight() == null
					|| "".equals(dataLensesVO.getAddRight()) ? 0 : Integer.valueOf(dataLensesVO
					.getAddRight()));
			spinnerAxis.setSelection(dataLensesVO.getAxisRight() == null
					|| "".equals(dataLensesVO.getAxisRight()) ? 0 : Integer.valueOf(dataLensesVO
					.getAxisRight()));
			spinnerCylinder.setSelection(dataLensesVO.getCylinderRight() == null
					|| "".equals(dataLensesVO.getCylinderRight()) ? 0 : Integer
					.valueOf(dataLensesVO.getCylinderRight()));
			spinnerTypeLens.setSelection(dataLensesVO.getTypeRight() == null
					|| "".equals(dataLensesVO.getTypeRight()) ? 0 : Integer.valueOf(dataLensesVO
					.getTypeRight()));
			editTextBC.setText(dataLensesVO.getBcRight().toString());
			editTextDia.setText(dataLensesVO.getDiaRight().toString());
		}
	}
}
