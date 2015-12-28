package com.aeo.mylensespro.activity;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
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
import android.widget.Toast;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.dao.LensesDataDAO;
import com.aeo.mylensespro.fragment.StatusFragment;
import com.aeo.mylensespro.util.AnalyticsApplication;
import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.DataLensesVO;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Object for intrinsic lock
    public static final Object sDataLock = new Object();

    public Toolbar toolbar;
    private boolean doubleBackToExitPressedOnce;

    BackupManager mBackupManager;
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
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        if (savedInstanceState == null) {
            Utility.replaceFragment(new StatusFragment(),
                    getSupportFragmentManager());
        }

        mBackupManager = new BackupManager(this);
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == R.id.menuHelp) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(R.string.msg_units);
//            builder.setCancelable(true);
//            builder.setPositiveButton(R.string.btn_ok, null);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_compra) {
            shop();
            toolbar.setTitle(R.string.nav_compra);
        } else {
            Utility.setScreen(id, toolbar, getSupportFragmentManager());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void shop() {
        LensesDataDAO lensesDataDAO = LensesDataDAO.getInstance(this);
        DataLensesVO lensesVO = lensesDataDAO
                .getById(lensesDataDAO.getLastIdLens());

        if (lensesVO != null) {
            String urlLeft = lensesVO.getBuy_site_left();
            String urlRight = lensesVO.getBuy_site_right();

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

}
