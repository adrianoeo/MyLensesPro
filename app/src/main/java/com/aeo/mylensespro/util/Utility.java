package com.aeo.mylensespro.util;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.fragment.AlarmFragment;
import com.aeo.mylensespro.fragment.DataLensesFragment;
import com.aeo.mylensespro.fragment.ListReplaceLensFragment;
import com.aeo.mylensespro.fragment.StatusFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class Utility {

    @SuppressLint("SimpleDateFormat")
    public static String formatDateDefault(String dateToFormat) {
        String date = null;
        try {
            if (dateToFormat != null) {
                date = new SimpleDateFormat("dd/MM/yyyy")
                        .format(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(dateToFormat));
            }
        } catch (ParseException e) {
        }
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateToSqlite(String dateToFormat) {
        String date = null;
        try {
            if (dateToFormat != null) {
                date = new SimpleDateFormat("yyyy-MM-dd")
                        .format(new SimpleDateFormat("dd/MM/yyyy")
                                .parse(dateToFormat));
            }
        } catch (ParseException e) {
        }
        return date;
    }

    public static void setScreen(int id, Toolbar toolbar, FragmentManager fm) {
        if (id == R.id.nav_status) {
            replaceFragment(new StatusFragment(), fm);
            toolbar.setTitle(R.string.title_status);
        } else if (id == R.id.nav_periodo) {
            replaceFragment(new ListReplaceLensFragment(), fm);
            toolbar.setTitle(R.string.title_periodo);
        } else if (id == R.id.nav_dados) {
            replaceFragment(new DataLensesFragment(), fm);
            toolbar.setTitle(R.string.title_dados);
        } else if (id == R.id.nav_notificacao) {
            replaceFragment(new AlarmFragment(), fm);
            toolbar.setTitle(R.string.nav_notificacao);
        }/* else if (id == R.id.nav_compra) {
            toolbar.setTitle(R.string.nav_compra);
        }*/
    }

    public static void replaceFragment(Fragment fragment, FragmentManager fm) {
        FragmentTransaction trans = fm.beginTransaction();

        trans.replace(R.id.fragment_container, fragment);

		/*
		 * IMPORTANT: The following lines allow us to add the fragment to the
		 * stack and return to it later, by pressing back
		 */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        // remove back stack
        // getFragmentManager().popBackStack(null,
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);

//        trans.addToBackStack(null);

        trans.commit();
    }

    public static void replaceFragmentWithBackStack(Fragment fragment, FragmentManager fm) {
        FragmentTransaction trans = fm.beginTransaction();

        trans.replace(R.id.fragment_container, fragment);

		/*
		 * IMPORTANT: The following lines allow us to add the fragment to the
		 * stack and return to it later, by pressing back
		 */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.addToBackStack(null);
        trans.commit();
    }

}
