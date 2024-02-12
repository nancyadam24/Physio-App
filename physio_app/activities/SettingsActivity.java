package com.geoxhonapps.physio_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.ADoctor;
import com.geoxhonapps.physio_app.RestUtilities.AManagerUser;
import com.geoxhonapps.physio_app.RestUtilities.AUser;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

public class SettingsActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        AUser user = StaticFunctionUtilities.getUser();
        ((TextView)findViewById(R.id.username)).setText(user.getUsername());
        ((TextView)findViewById(R.id.displayName)).setText(user.getDisplayName());
        ((TextView)findViewById(R.id.email)).setText(user.getEmail());
        ((TextView)findViewById(R.id.SSN)).setText(user.getSSN());
        ((TextView)findViewById(R.id.address)).setText(user.getAddress());
        switch(user.getAccountType()){
            case Manager:
                ((TextView)findViewById(R.id.userType)).setText("Μάνατζερ");
                break;
            case Doctor:
                ((TextView)findViewById(R.id.userType)).setText("Γιατρός");
                break;
            case Patient:
                ((TextView)findViewById(R.id.userType)).setText("Ασθενής");
                break;
            default:
                ((TextView)findViewById(R.id.userType)).setText("Άλλο");
                break;
        }
        CardView logout = findViewById(R.id.logoutCard);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ContextFlowUtilities.getCurrentView())
                        .setTitle("Σίγουρα θες να αποσυνδεθείς;")
                        .setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                StaticFunctionUtilities.attemptLogout();
                            }
                        })
                        .setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
}