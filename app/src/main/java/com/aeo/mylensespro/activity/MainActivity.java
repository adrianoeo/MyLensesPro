package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.AlarmDAO;
import com.aeo.mylensespro.dao.DataLensesDAO;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.fragment.StatusFragment;
import com.aeo.mylensespro.util.MyLensesApplication;
import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.DataLensesVO;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    private boolean doubleBackToExitPressedOnce;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the shared Tracker instance.
        MyLensesApplication application = (MyLensesApplication) getApplication();
        mTracker = application.getDefaultTracker();


        //authentication
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            loadLoginView();
        } else if (currentUser != null && !currentUser.getBoolean("emailVerified")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
                    ParseUser.logOut();
                    loadLoginView();
//                }
//            };
//            builder.setMessage(R.string.login_email_not_confirmed)
//                    .setTitle(R.string.login_error_title)
//                    .setPositiveButton(android.R.string.ok, onClickListener);
//
//            AlertDialog dialog = builder.create();
//            dialog.show();

        } else {
            if (savedInstanceState == null) {
                View view =  navigationView.getHeaderView(0);
                TextView email = (TextView)  view.findViewById(R.id.email);
                email.setText(currentUser.getEmail());

                Utility.replaceFragment(new StatusFragment(), getSupportFragmentManager());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlarmDAO.getInstance(getApplicationContext()).getAlarm();
//        DataLensesDAO.getInstance(getApplicationContext()).getLastDataLensesAsync();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.msg_press_once_again_to_exit,
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AlarmDAO.alarmVO == null) {
            AlarmDAO.getInstance(getApplicationContext()).getAlarm();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_compra) {
            shop();
            toolbar.setTitle(R.string.nav_compra);
        } else if (id == R.id.nav_logout) {
            ParseUser.logOut();
            loadLoginView();
        } else {
            Bundle bundle = null;
//            if (alarmVO != null) {
//                bundle = new Bundle();
//                bundle.putInt("hour", alarmVO.getHour());
//                bundle.putInt("minute", alarmVO.getMinute());
//                bundle.putInt("days_before", alarmVO.getDaysBefore());
//                bundle.putInt("remind_every_day", alarmVO.getRemindEveryDay());
//            }

            Utility.setScreen(id, toolbar, getSupportFragmentManager(), bundle);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void shop() {
        DataLensesDAO dataLensesDAO = DataLensesDAO.getInstance(this);
        DataLensesVO lensesVO = dataLensesDAO.getLastDataLenses();

        if (lensesVO != null) {
            String urlLeft = lensesVO.getBuySiteLeft();
            String urlRight = lensesVO.getBuySiteRight();

            if ((urlLeft == null || "".equals(urlLeft))
                    && (urlRight == null || "".equals(urlRight))) {
                showAlertDialog();
            } else if (urlLeft.equals(urlRight)) {
                if (!urlLeft.startsWith("http://")
                        && !urlLeft.startsWith("https://")) {
                    urlLeft = "http://" + urlLeft;
                }

                Uri uri = Uri.parse(urlLeft);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                if (urlLeft != null && !"".equals(urlLeft)) {
                    if (!urlLeft.startsWith("http://")
                            && !urlLeft.startsWith("https://")) {
                        urlLeft = "http://" + urlLeft;
                    }
                    Uri uriLeft = Uri.parse(urlLeft);
                    startActivity(new Intent(Intent.ACTION_VIEW, uriLeft));
                }
                if (urlRight != null && !"".equals(urlRight)) {
                    if (!urlRight.startsWith("http://")
                            && !urlRight.startsWith("https://")) {
                        urlRight = "http://" + urlRight;
                    }

                    Uri uriRight = Uri.parse(urlRight);
                    startActivity(new Intent(Intent.ACTION_VIEW, uriRight));
                }
            }
        } else {
            showAlertDialog();
        }
    }

    // Opening browser
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_no_buy_site);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.btn_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("http://www.google.com")));
                    }
                });
        builder.setNegativeButton(R.string.btn_no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        /*So ‘back’ is pressed, the app navigates to the previous activity.
          We need to clear the stack history and set the LoginActivity as the start of
          the history stack. Modify loadLoginView() as shown.*/
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //Seta Alarme para notificações
            TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(getApplicationContext());
            AlarmDAO alarmDAO = AlarmDAO.getInstance(getApplicationContext());
            alarmDAO.setAlarm(timeLensesDAO.getLastLens());
        }
    }

}
