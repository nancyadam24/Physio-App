package com.geoxhonapps.physio_app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

public class ParentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextFlowUtilities.setCurrentView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContextFlowUtilities.setCurrentView(this);
    }
}
