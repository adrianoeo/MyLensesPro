package com.aeo.mylensespro.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
    public static String formatDateDefault(String dateToFormat, Context context) {
        String date = null;
        try {
            if (dateToFormat != null) {
                String format = context.getResources().getString(R.string.locale);
                date = new SimpleDateFormat(format)
                        .format(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(dateToFormat));
            }
        } catch (ParseException e) {
        }
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateToSqlite(String dateToFormat, Context context) {
        String date = null;
        try {
            if (dateToFormat != null) {
                String format = context.getResources().getString(R.string.locale);
                date = new SimpleDateFormat("yyyy-MM-dd")
                        .format(new SimpleDateFormat(format)
                                .parse(dateToFormat));
            }
        } catch (ParseException e) {
        }
        return date;
    }

    public static void setScreen(int id, Toolbar toolbar, FragmentManager fm, Bundle bundle) {
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
            AlarmFragment alarmFragment = new AlarmFragment();
            alarmFragment.setArguments(bundle);

            replaceFragment(alarmFragment, fm);
            toolbar.setTitle(R.string.nav_notificacao);
        }
    }

    public static void replaceFragment(Fragment fragment, FragmentManager fm) {
        FragmentTransaction trans = fm.beginTransaction();

        trans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

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

        trans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        trans.replace(R.id.fragment_container, fragment);

		/*
         * IMPORTANT: The following lines allow us to add the fragment to the
		 * stack and return to it later, by pressing back
		 */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.addToBackStack(null);
        trans.commit();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected()
                && connectivityManager.getActiveNetworkInfo().isAvailable();
    }

    public static int[] getNetworkType(Context context) {
        ConnectivityManager connectivityManager
                = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable()) {
            return new int[]{activeNetwork.getType(), activeNetwork.getSubtype()};
        }

        return null;
    }

    public static boolean isConnectionFast(Context context) {
        int[] types = getNetworkType(context);
        if (types != null) {
            return Connectivity.isConnectionFast(types[0], types[1]);
        }
        return false;
    }
}
