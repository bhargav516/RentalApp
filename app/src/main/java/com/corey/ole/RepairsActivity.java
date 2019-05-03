package com.corey.ole;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class RepairsActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase mDb;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repairs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setDrawerData(navigationView);

        mDb = FirebaseDatabase.getInstance();
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final EditText repairInputBox = findViewById(R.id.repairEditText);
        Button send = findViewById(R.id.repairButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repair = repairInputBox.getText().toString();
                if (TextUtils.isEmpty(repair)) {
                    // don't do anything if nothing was added
                    repairInputBox.requestFocus();
                } else {
                    // clear edit text, post comment
                    repairInputBox.setText("");
                    postNewRepair(repair);
                }
            }
        });
    }

    private void postNewRepair(String repair) {
        Date now = new Date();
        DatabaseReference repairRef = mDb.getReference("users/" + mUid + "/repairs").push();
        repairRef.child("Date").setValue(now);
        repairRef.child("Request").setValue(repair);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.repairs, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, TenantHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(this, MessagesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rent) {
            Intent intent = new Intent(this, RentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lease) {

        } else if (id == R.id.nav_repair) {
            // Do nothing
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, TenantTenantProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
