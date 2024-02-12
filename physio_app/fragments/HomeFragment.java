package com.geoxhonapps.physio_app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.geoxhonapps.physio_app.RestUtilities.AAppointment;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.RestUtilities.APatientUser;
import com.geoxhonapps.physio_app.activities.AddDoctorActivity;
import com.geoxhonapps.physio_app.activities.AddPatientActivity;
import com.geoxhonapps.physio_app.activities.AddServiceActivity;
import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.EUserType;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        TextView userText = rootView.findViewById(R.id.userDisplayName);
        userText.setText(StaticFunctionUtilities.getUser().getDisplayName());
        FloatingActionButton bottomButton = rootView.findViewById(R.id.floatingActionButton);
        if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Patient){
            bottomButton.hide();
        }
        CardView nextAppointmentView = rootView.findViewById(R.id.nextAppointmentCard);
        switch(StaticFunctionUtilities.getUser().getAccountType()){
            case Manager:
                nextAppointmentView.setVisibility(View.GONE);
                break;
            case Doctor:
                ADoctorUser doctorUser = ((ADoctorUser) StaticFunctionUtilities.getUser());
                if(doctorUser.getNextAppointment() == null){
                    nextAppointmentView.setVisibility(View.GONE);
                }else{
                    AAppointment nextAppointment = doctorUser.getNextAppointment();
                    TextView nextAppointmentText = rootView.findViewById(R.id.nextAppointmentName);
                    TextView nextAppointmentDate = rootView.findViewById(R.id.nextAppointmentDate);
                    nextAppointmentText.setText(nextAppointment.getAssociatedUser().getDisplayName());
                    nextAppointmentDate.setText(nextAppointment.getGlobalDateString());
                }
                break;
            case Patient:
                APatientUser patientUser = ((APatientUser) StaticFunctionUtilities.getUser());
                if(patientUser.getNextAppointment() == null){
                    nextAppointmentView.setVisibility(View.GONE);
                }else{
                    AAppointment nextAppointment = patientUser.getNextAppointment();
                    TextView nextAppointmentText = rootView.findViewById(R.id.nextAppointmentName);
                    TextView nextAppointmentDate = rootView.findViewById(R.id.nextAppointmentDate);
                    nextAppointmentText.setText(nextAppointment.getAssociatedUser().getDisplayName());
                    nextAppointmentDate.setText(nextAppointment.getGlobalDateString());
                }
                break;

        }

        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ContextFlowUtilities.getCurrentView(), bottomButton);
                if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Manager){
                    popup.getMenuInflater().inflate(R.menu.fab_menu_manager, popup.getMenu());
                }else {
                    popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_add_patient:
                                ContextFlowUtilities.moveTo(AddPatientActivity.class, true);
                                return true;
                            case R.id.action_add_service:
                                ContextFlowUtilities.moveTo(AddServiceActivity.class, true);
                                return true;
                            case R.id.action_add_doctor:
                                ContextFlowUtilities.moveTo(AddDoctorActivity.class, true);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        return rootView;
    }
}
