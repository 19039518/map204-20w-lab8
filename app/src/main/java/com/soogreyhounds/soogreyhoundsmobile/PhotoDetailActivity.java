package com.soogreyhounds.soogreyhoundsmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soogreyhounds.soogreyhoundsmobile.Photo;
import com.soogreyhounds.soogreyhoundsmobile.PhotoStorage;
import com.soogreyhounds.soogreyhoundsmobile.PhotoViewerActivity;
import com.soogreyhounds.soogreyhoundsmobile.R;

public class PhotoDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CONTACT = 1;
    public static String EXTRA_UUID = "com.soogreyhounds.soogreyhoundsmobile.photo.uuid";
    private Photo mPhoto;
    private EditText mUUIDEditText;
    private EditText mTitleEditText;
    private EditText mURLEditText;
    private Button mPersonButton;

    private boolean mEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);



        mUUIDEditText = findViewById(R.id.photo_uuid);
        mTitleEditText = findViewById(R.id.photo_title);
        mURLEditText = findViewById(R.id.photo_url);
        mPersonButton = findViewById(R.id.choose_person_photo);
        mEditing = false;

        mPhoto = new Photo();
        if (getIntent().hasExtra(EXTRA_UUID)) {
            mEditing = true;
            String uuid = getIntent().getStringExtra(EXTRA_UUID);
            mPhoto = PhotoStorage.get(this).getPhoto(uuid);
            mUUIDEditText.setText(uuid);
            mUUIDEditText.setEnabled(false);
            mTitleEditText.setText(mPhoto.getTitle());
            mURLEditText.setText(mPhoto.getURL());
            if (mPhoto.getPerson() != null) {
                mPersonButton.setText(mPhoto.getPerson());
            }
        }

        mPersonButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        Button saveButton = findViewById(R.id.save_photo);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUUIDEditText.getText().toString().equals("")) {
                    Toast.makeText(v.getContext(), "Please enter a UUID", Toast.LENGTH_LONG).show();
                    return;
                }
                mPhoto.setUUID(mUUIDEditText.getText().toString());
                mPhoto.setTitle(mTitleEditText.getText().toString());
                mPhoto.setURL(mURLEditText.getText().toString());
                if (mEditing) {
                    PhotoStorage.get(v.getContext()).updatePhoto(mPhoto);
                } else {
                    PhotoStorage.get(v.getContext()).addPhoto(mPhoto);
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        Button photoViewerButton = findViewById(R.id.photoViewerButton);
        photoViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PhotoViewerActivity.class);
                startActivity(intent);
            }
        });


        mPersonButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        PackageManager packageManager = getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mPersonButton.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where" clause here
            Cursor c = getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data -
                // that is your person's name
                c.moveToFirst();
                String person = c.getString(0);
                mPhoto.setPerson(person);
                mPersonButton.setText(person);
            } finally {
                c.close();
            }
        }
    }

}
