package com.corey.ole;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* Adapter for Properties in Landlord's Home */

public class PropertyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Property> mProperty;
    private OnItemClickListener mListener;

    public PropertyAdapter(Context context, ArrayList<Property> propertyView) {
        mContext = context;
        mProperty = propertyView;

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.property_cell, viewGroup, false);
        return new PropertyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Property property = mProperty.get(i);
        ((PropertyViewHolder) viewHolder).bind(property);
    }

    @Override
    public int getItemCount() {
        return mProperty.size();
    }


    /* PropertyViewHolder sets the image and info of the property on the UI */
    class PropertyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mPropertyLayout;
        private TextView mTitle;
        private TextView mStreet;
        private TextView mCityStateZip;
        private TextView mTenants;
        private ImageView mImage;

        public static final String EXTRA_PROPERTYNAME = "propertyName";


        public PropertyViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mPropertyLayout = itemView.findViewById(R.id.property_cell);
            mTitle = mPropertyLayout.findViewById(R.id.property_cell_name);
            mStreet = mPropertyLayout.findViewById(R.id.property_cell_street);
            mCityStateZip = mPropertyLayout.findViewById(R.id.property_cell_csz);
            mTenants = mPropertyLayout.findViewById(R.id.property_cell_tenants);
            mImage = mPropertyLayout.findViewById(R.id.property_cell_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }

                }
            });
        }

        void bind(Property property) {
            int t = property.getNumTenants();
            mTenants.setText(t + " Tenants");

            mTitle.setText(property.getName());
            mStreet.setText(property.getStreet());
            mCityStateZip.setText(property.getCityStateZip());

            try {
                final File localFile = File.createTempFile("images", "jpg");

                if (property.getImagePath() != null && property.getImagePath().length() != 0) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageStorage = storageRef.child(property.getImagePath());
                    imageStorage.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Uri uri = Uri.fromFile(localFile);
                                    mImage.setImageURI(uri);
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

    }
}