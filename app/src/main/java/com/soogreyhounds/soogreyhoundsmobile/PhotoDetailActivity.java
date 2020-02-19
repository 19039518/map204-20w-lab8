package com.soogreyhounds.soogreyhoundsmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.soogreyhounds.soogreyhoundsmobile.Photo;
import com.soogreyhounds.soogreyhoundsmobile.PhotoStorage;
import com.soogreyhounds.soogreyhoundsmobile.PhotoViewerActivity;
import com.soogreyhounds.soogreyhoundsmobile.R;

import java.io.File;
import java.util.List;

public class PhotoDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    public static String EXTRA_UUID = "com.soogreyhounds.soogreyhoundsmobile.photo.uuid";
    private Photo mPhoto;
    private File mPhotoFile;
    private EditText mUUIDEditText;
    private EditText mTitleEditText;
    private EditText mURLEditText;
    private Button mPersonButton;
    private Button mShareButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

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
        mShareButton = (Button) findViewById(R.id.share_photo_button);
        mPhotoButton = (ImageButton) findViewById(R.id.camera_button);
        mPhotoView = (ImageView) findViewById(R.id.photo);

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
            mPhotoFile = PhotoStorage.get(this).getPhotoFile(mPhoto);

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
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        if (!canTakePhoto) {
            mPhotoButton.setVisibility(View.GONE);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getBaseContext(),
                        "com.soogreyhounds.soogreyhoundsmobile.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getBaseContext()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getBaseContext().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        updatePhotoView();

        mShareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getPhotoDetails());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.photo_details_subject));
                i = Intent.createChooser(i, getString(R.string.send_photo));
                startActivity(i);
            }
        });
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
            String[] queryFields = new String[]{
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
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(this,
                    "com.soogreyhounds.soogreyhoundsmobile.fileprovider",
                    mPhotoFile);
            this.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }


    private String getPhotoDetails() {
        String details = getString(R.string.photo_details, mPhoto.getTitle(), mPhoto.getURL());
        return details;

    }
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), this);
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}
