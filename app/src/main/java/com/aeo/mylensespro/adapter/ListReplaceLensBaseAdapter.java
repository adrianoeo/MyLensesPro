package com.aeo.mylensespro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
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
    private FragmentManager fragmentManager;

    private ListFragment listFragment;

    public ListReplaceLensBaseAdapter(Context context, List<TimeLensesVO> list,
                                      FragmentManager fragmentManager,
                                      ListFragment listFragment) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
        this.listFragment = listFragment;
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

//        if (position == getCount()-1) {
//            ListLensesTask task = new ListLensesTask(context, this, fragmentManager, listFragment, list);
//            task.execute();
//        }

//        TextView idLens;
//        TextView dateLeft;
//        TextView dateRight;
//        TextView timeLeft;
//        TextView timeRight;
//        TextView txtLensLeft;
//        TextView txtLensRight;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = getViewHolder(mInflater.inflate(R.layout.fragment_item_list_lens, null));


//            idLens = (TextView) view.findViewById(R.id.textViewIdLens);
//
//            dateLeft = (TextView) view.findViewById(R.id.textViewDateReplaceLensLeft);
//            dateRight = (TextView) view.findViewById(R.id.textViewDateReplaceLensRight);
//            timeLeft = (TextView) view.findViewById(R.id.textViewTimeReplaceLensLeft);
//            timeRight = (TextView) view.findViewById(R.id.textViewTimeReplaceLensRight);
//            txtLensLeft = (TextView) view.findViewById(R.id.textViewDescReplaceLensLeft);
//            txtLensRight = (TextView) view.findViewById(R.id.textViewDescReplaceLensRight);

            TimeLensesVO lenses = list.get(position);
            String typeLeft = null;
            String typeRight = null;

            if (lenses.getTypeLeft() == 0) {
                typeLeft = "Day(s)";
            } else if (lenses.getTypeLeft() == 1) {
                typeLeft = "Month(s)";
            } else if (lenses.getTypeLeft() == 2) {
                typeLeft = "Year(s)";
            }
            if (lenses.getTypeRight() == 0) {
                typeRight = "Day(s)";
            } else if (lenses.getTypeRight() == 1) {
                typeRight = "Month(s)";
            } else if (lenses.getTypeRight() == 2) {
                typeRight = "Year(s)";
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            viewHolder.idLens.setText(lenses.getId().toString());
            viewHolder.dateLeft.setText(lenses.getDateLeft());
            viewHolder.dateRight.setText(lenses.getDateRight());
            viewHolder.timeLeft.setText(new StringBuilder()
                    .append(lenses.getExpirationLeft()).append(" ")
                    .append(typeLeft));
            viewHolder.timeRight.setText(new StringBuilder()
                    .append(lenses.getExpirationRight()).append(" ")
                    .append(typeRight));

//            dateLeft.setText(lenses.getDateLeft());
//            dateRight.setText(lenses.getDateRight());
//            timeLeft.setText(new StringBuilder()
//                    .append(lenses.getExpirationLeft()).append(" ")
//                    .append(typeLeft));
//            timeRight.setText(new StringBuilder()
//                    .append(lenses.getExpirationRight()).append(" ")
//                    .append(typeRight));
//
//            idLens.setText(lenses.getId().toString());

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
    }
}
