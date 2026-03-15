package com.example.finaproject.data.MyProfileTable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSyncService extends Service {
    public UserSyncService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //read the data that received within the intent
        if (intent != null && intent.hasExtra("profile_extra")) {
            Profile profile = (Profile) intent.getSerializableExtra("profile_extra");
            saveMyTaskToFirebase(profile);
        }
        // START_NOT_STICKY means if the system kills the service, don't recreate it automatically
        return START_NOT_STICKY;
    }


    private void saveMyTaskToFirebase(Profile profile) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("profile");
        String key = myRef.push().getKey();

        profile.setKid(key);


        myRef.child(key).setValue(profile).addOnCompleteListener(fbTask -> {
            if (fbTask.isSuccessful()) {
                // In a service, use context from getApplicationContext() for Toasts
                Toast.makeText(getApplicationContext(), "Sync Successful", Toast.LENGTH_SHORT).show();
            }
            // Stop the service once the work is done to save battery/RAM
            stopSelf();
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We are using a Started Service, not a Bound Service
    }
}

