package com.omerasoftware.update_pop_up_immdate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private AppUpdateManager mAppUpdateManager;
    private static  final int RC_APP_UPDATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if(result.updateAvailability() ==
                        UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){  // FLEXIBLE

                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE,
                                MainActivity.this,RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

    //    mAppUpdateManager.registerListener(installStateUpdatedListener);

    }

    private InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED){
                        showCompletedUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED){
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.d("gokulnathan",""+ state.installStatus());
                    }
                }
            };

    @Override
    protected void onStop() {
      // if(mAppUpdateManager != null) mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private void showCompletedUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);



        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAppUpdateManager.completeUpdate();

            }
        });

        snackbar.show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == RC_APP_UPDATE && resultCode != RESULT_OK){

            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if(result.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){  // FLEXIBLE

                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE,
                                MainActivity.this,RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }


                }

            }
        });


    }
}