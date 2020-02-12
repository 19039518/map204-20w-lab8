package com.soogreyhounds.soogreyhoundsmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PhotoDetailActivity extends AppCompatActivity {
    public static String EXTRA_UUID = "com.soogreyhounds.soogreyhoundsmobile.photo.uuid";
    private EditText mUUIDEditText;
    private EditText mTitleEditText;
    private EditText mURLEditText;
    private Photo mPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        mUUIDEditText = findViewById(R.id.photo_uuid);
        mTitleEditText = findViewById(R.id.photo_title);
        mURLEditText = findViewById(R.id.photo_url);

        Button saveButton = findViewById(R.id.save_photo);
        mEditing = false;

        mPhoto = new Photo();
        if (getIntent().hasExtra(EXTRA_UUID)) {
            mEditing = true;
            String uuid = getIntent().getStringExtra(EXTRA_UUID);
            mPhoto = PhotoStorage.get(this).getPhoto(uuid);
            mUUIDEditText.setText(uuid);
            mUUIDEditText.setEnabled(false);
            mTitleEditText.setText(mPhoto.getTitle());
            mURLEditText.setText(mPhoto.getURL()); }


        saveButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              if (mUUIDEditText.getText().toString().equals("")) {
                                                  Toast.makeText(v.getContext(), "Please enter a UUID", Toast.LENGTH_LONG).show();
                                                  return;
                                              }
                                              mPhoto.setUUID(mUUIDEditText.getText().toString());
                                              mPhoto.setTitle(mTitleEditText.getText().toString());
                                              if (mEditing) {
                                                  PhotoStorage.get(v.getContext()).updatePhoto(mPhoto);
                                              } else {
                                                  PhotoStorage.get(v.getContext()).addPhoto(mPhoto);
                                              }mPhoto.setURL(mURLEditText.getText().toString());

                                              PhotoStorage.get(v.getContext()).addPhoto(mPhoto);
                                              setResult(Activity.RESULT_OK);
                                              finish();
                                          }
                                      }
        Button photoViewerButton = findViewById(R.id.photoViewerButton);
        photoViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PhotoViewerActivity.class);
                startActivity(intent);
            }
        });

    }
}
