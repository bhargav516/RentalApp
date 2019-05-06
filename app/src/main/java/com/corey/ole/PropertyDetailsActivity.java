package com.corey.ole;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PropertyDetailsActivity extends NavDrawerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView image;
    private TextView name;
    private TextView address;
    private FirebaseDatabase database;
    private String propertyId;
    private String propertyName;
    private RecyclerView policiesRecycler;
    private RecyclerView notesRecycler;
    private Button viewTenantButton;
    private String landlordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);
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

        image = findViewById(R.id.property_details_image);
        name = findViewById(R.id.property_details_name);
        address = findViewById(R.id.property_details_address);
        viewTenantButton = findViewById(R.id.property_details_view_tenants_button);

        policiesRecycler = findViewById(R.id.property_details_policies_recycler_view);
        policiesRecycler.setHasFixedSize(true);
        policiesRecycler.setLayoutManager(new LinearLayoutManager(this));
        notesRecycler = findViewById(R.id.property_details_notes_recycler_view);
        notesRecycler.setHasFixedSize(true);
        notesRecycler.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        propertyId = intent.getStringExtra("propertyId");
        landlordId = intent.getStringExtra("landlordId");

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("property");
        DatabaseReference property = ref.child(propertyId);

        property.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Property property = dataSnapshot.getValue(Property.class);
                propertyName = property.getName();
                name.setText(propertyName);
                address.setText(property.getStreet() + property.getCityStateZip());
                ArrayList<String> notes = property.getNotes();
                if (notes != null) {
                    AnnouncementAdapter noteAdapter = new AnnouncementAdapter(property.getNotes());
                    notesRecycler.setAdapter(noteAdapter);
                }
                ArrayList<String> policies = property.getPolicies();
                if (policies != null) {
                    AnnouncementAdapter policyAdapter = new AnnouncementAdapter(property.getPolicies());
                    policiesRecycler.setAdapter(policyAdapter);

                }
                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                    if (property.getImagePath() != null && property.getImagePath().length() != 0) {
                        StorageReference imageStorage = storageRef.child(property.getImagePath());
                        imageStorage.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Uri uri = Uri.fromFile(localFile);
                                        image.setImageURI(uri);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        viewTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTenantList();

            }
        });

    }

    private void startTenantList(){
        Intent intent = new Intent(this, TenantListActivity.class);
        intent.putExtra("name", propertyName);
        intent.putExtra("PropertyID", propertyId);

        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            Intent intent = new Intent(this, EditAddPropertyActivity.class);
            intent.putExtra("propertyId", propertyId);
            intent.putExtra("landlordId", landlordId);
            intent.putExtra("isAdd", false);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, LandlordHomeActivity.class);
            intent.putExtra("landlordId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(this, LandlordMessagesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, LandlordProfileActivity.class);
            startActivity(intent);
            return true;
        }  else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
