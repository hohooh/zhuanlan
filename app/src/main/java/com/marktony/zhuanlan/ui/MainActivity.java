package com.marktony.zhuanlan.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.marktony.zhuanlan.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
   private View headview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        headview=navigationView.inflateHeaderView(R.layout.nav_header_main_login);
        navigationView.setCheckedItem(R.id.nav_product);
        GlobalFragment fragment = GlobalFragment.newInstance();
        fragment.setType(GlobalFragment.TYPE_PRODUCT);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,fragment)
                .commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_copyright) {

            MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.action_copyright)
                    .content(R.string.copyright_content)
                    .neutralText(R.string.got_it)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                          dialog.dismiss();
                        }
                    }).build();

            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_product) {

            GlobalFragment fragment = GlobalFragment.newInstance();
            fragment.setType(GlobalFragment.TYPE_PRODUCT);
            replaceFragment(fragment);

        } else if (id == R.id.nav_life) {

            GlobalFragment fragment = GlobalFragment.newInstance();
            fragment.setType(GlobalFragment.TYPE_LIFE);
            replaceFragment(fragment);

        } else if (id == R.id.nav_music) {

            GlobalFragment fragment = GlobalFragment.newInstance();
            fragment.setType(GlobalFragment.TYPE_MUSIC);
            replaceFragment(fragment);

        } else if (id == R.id.nav_emotion) {

            GlobalFragment fragment = GlobalFragment.newInstance();
            fragment.setType(GlobalFragment.TYPE_EMOTION);
            replaceFragment(fragment);

        } else if (id == R.id.nav_profession) {

            GlobalFragment fragment = GlobalFragment.newInstance();
            fragment.setType(GlobalFragment.TYPE_FINANCE);
            replaceFragment(fragment);

        } else if (id == R.id.nav_zhihu){

            GlobalFragment fragment = GlobalFragment.newInstance();
            fragment.setType(GlobalFragment.TYPE_ZHIHU);
            replaceFragment(fragment);

        } else if (id == R.id.nav_user_define){

            replaceFragment(new UserDefineIdsFragment());

        } else if (id == R.id.nav_about){
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,fragment)
                .commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
