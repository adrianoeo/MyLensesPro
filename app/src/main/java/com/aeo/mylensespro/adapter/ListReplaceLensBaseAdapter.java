package com.aeo.mylensespro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.vo.TimeLensesVO;

import java.util.List;

public class ListReplaceLensBaseAdapter extends BaseAdapter {
    private List<TimeLensesVO> list;
    private Context context;

    public ListReplaceLensBaseAdapter(Context context, List<TimeLensesVO> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = getViewHolder(mInflater.inflate(R.layout.fragment_item_list_lens, null));

            TimeLensesVO lenses = list.get(position);
            String typeLeft = null;
            String typeRight = null;

            if (lenses.getTypeLeft() == 0) {
                typeLeft = context.getResources().getString(R.string.str_days);
            } else if (lenses.getTypeLeft() == 1) {
                typeLeft = context.getResources().getString(R.string.str_months);
            } else if (lenses.getTypeLeft() == 2) {
                typeLeft = context.getResources().getString(R.string.str_years);
            }
            if (lenses.getTypeRight() == 0) {
                typeRight = context.getResources().getString(R.string.str_days);
            } else if (lenses.getTypeRight() == 1) {
                typeRight = context.getResources().getString(R.string.str_months);
            } else if (lenses.getTypeRight() == 2) {
                typeRight = context.getResources().getString(R.string.str_years);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            viewHolder.idLens.setText(lenses.getId().toString());
            viewHolder.objectIdLens.setText(lenses.getObjectId() == null
                    ? "" : lenses.getObjectId().toString());
            viewHolder.dateLeft.setText(lenses.getDateLeft());
            viewHolder.dateRight.setText(lenses.getDateRight());
            viewHolder.timeLeft.setText(new StringBuilder()
                    .append(lenses.getExpirationLeft()).append(" ")
                    .append(typeLeft));
            viewHolder.timeRight.setText(new StringBuilder()
                    .append(lenses.getExpirationRight()).append(" ")
                    .append(typeRight));

            setColor(position, viewHolder);

        } else {
            view = getViewHolder(convertView);

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            setColor(position, viewHolder);

        }

        return view;
    }

    private void setColor(int position, ViewHolder holder) {
        if (position > 0) {
            holder.txtLensLeft.setTextColor(Color.GRAY);
            holder.txtLensRight.setTextColor(Color.GRAY);
            holder.dateLeft.setTextColor(Color.GRAY);
            holder.dateRight.setTextColor(Color.GRAY);
            holder.timeLeft.setTextColor(Color.GRAY);
            holder.timeRight.setTextColor(Color.GRAY);

//                txtLensLeft.setTextColor(Color.GRAY);
//                txtLensRight.setTextColor(Color.GRAY);
//                dateLeft.setTextColor(Color.GRAY);
//                dateRight.setTextColor(Color.GRAY);
//                timeLeft.setTextColor(Color.GRAY);
//                timeRight.setTextColor(Color.GRAY);
        } else {
            holder.txtLensLeft.setTextColor(Color.BLACK);
            holder.txtLensRight.setTextColor(Color.BLACK);
            holder.dateLeft.setTextColor(Color.BLACK);
            holder.dateRight.setTextColor(Color.BLACK);
            holder.timeLeft.setTextColor(Color.BLACK);
            holder.timeRight.setTextColor(Color.BLACK);
//                txtLensLeft.setTextColor(Color.BLACK);
//                txtLensRight.setTextColor(Color.BLACK);
//                dateLeft.setTextColor(Color.BLACK);
//                dateRight.setTextColor(Color.BLACK);
//                timeLeft.setTextColor(Color.BLACK);
//                timeRight.setTextColor(Color.BLACK);
        }
    }

    private View getViewHolder(View view) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.idLens = (TextView) view.findViewById(R.id.textViewIdLens);
        viewHolder.objectIdLens = (TextView) view.findViewById(R.id.textViewObjectIdLens);
        viewHolder.dateLeft = (TextView) view.findViewById(R.id.textViewDateReplaceLensLeft);
        viewHolder.dateRight = (TextView) view.findViewById(R.id.textViewDateReplaceLensRight);
        viewHolder.timeLeft = (TextView) view.findViewById(R.id.textViewTimeReplaceLensLeft);
        viewHolder.timeRight = (TextView) view.findViewById(R.id.textViewTimeReplaceLensRight);
        viewHolder.txtLensLeft = (TextView) view.findViewById(R.id.textViewDescReplaceLensLeft);
        viewHolder.txtLensRight = (TextView) view.findViewById(R.id.textViewDescReplaceLensRight);

        view.setTag(viewHolder);

        return view;
    }

    static class ViewHolder {
        public TextView dateLeft;
        public TextView dateRight;
        public TextView txtLensLeft;
        public TextView txtLensRight;
        public TextView timeLeft;
        public TextView timeRight;
        public TextView idLens;
        public TextView objectIdLens;
    }
}
