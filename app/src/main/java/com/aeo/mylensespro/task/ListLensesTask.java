package com.aeo.mylensespro.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.adapter.ListReplaceLensBaseAdapter;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.vo.TimeLensesVO;

import java.util.List;

/**
 * Created by adriano on 08/01/2016.
 */
public class ListLensesTask extends AsyncTask<String, Void, List<TimeLensesVO>> {
    private Context context;
    private ListReplaceLensBaseAdapter mListAdapter;
    private FragmentManager fragmentManager;
    private ListFragment listFragment;
    private List<TimeLensesVO> listTimeLensesVO;

    public ListLensesTask(Context ctx,
                          ListReplaceLensBaseAdapter mListAdapter,
                          FragmentManager fragmentManager,
                          ListFragment listFragment,
                          List<TimeLensesVO> listTimeLensesVO) {
        context = ctx;
        this.mListAdapter = mListAdapter;
        this.fragmentManager = fragmentManager;
        this.listFragment = listFragment;
        this.listTimeLensesVO = listTimeLensesVO;
    }

    @Override
    protected List<TimeLensesVO> doInBackground(String... params) {
        return TimeLensesDAO.getInstance(context).getListLensesLimit(listTimeLensesVO);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(List<TimeLensesVO> listLens) {
        if (listLens != null && listLens.size() > 0) {
            mListAdapter = new ListReplaceLensBaseAdapter(context, listLens, fragmentManager, listFragment);
            listFragment.setListAdapter(mListAdapter);
            listFragment.setSelection(listLens.size()-1);
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{context.getString(R.string.msg_insert_time_replace)});
            listFragment.setListAdapter(adapter);
        }

    }
}