package com.soogreyhounds.soogreyhoundsmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }
        public void bindPhoto(Photo photo) {
            mTitleTextView.setText(photo.getTitle());
        }
    }
    private RecyclerView mPhotoRecyclerView;
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<Photo> mPhotos;
        public PhotoAdapter(List<Photo> photos) {
            mPhotos = photos;
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            TextView textView = new TextView(getBaseContext());
            return new PhotoHolder(textView);
        }
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            Photo photo = mPhotos.get(position);
            photoHolder.bindPhoto(photo);
        }
        @Override
        public int getItemCount() {
            return mPhotos.size();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotoRecyclerView = findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateList();


    }
    private void updateList() {
        mPhotoRecyclerView.setAdapter(new PhotoAdapter(PhotoStorage.get(this).getPhotos()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                return true;
            case R.id.create_account:
                Intent createAccountIntent = new Intent(this, CreateAccountActivity.class);
                startActivity(createAccountIntent);
                return true;
            case R.id.edit_profile:
                Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
                startActivity(editProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
            case R.id.add_photo:
                Photo photo = new Photo();
                photo.setUUID("234243-dsfsa-23sdf");
                photo.setTitle("A photo");
                photo.setURL("https://www.test.com/Test.jpg");
                photo.setNote("This is a note");
                PhotoStorage.get(getBaseContext()).addPhoto(photo);
                updateList();
                return true;
        }
    }
}