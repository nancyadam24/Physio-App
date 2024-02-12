package com.geoxhonapps.physio_app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.ADoctor;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.RestUtilities.AManagerUser;
import com.geoxhonapps.physio_app.RestUtilities.APatientUser;
import com.geoxhonapps.physio_app.RestUtilities.AUser;
import com.geoxhonapps.physio_app.RestUtilities.EUserType;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import java.util.ArrayList;

class DoctorAdapter extends ArrayAdapter<ADoctor> {

    public DoctorAdapter(Context context, ArrayList<ADoctor> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_doctors_listitem, parent, false);
        }
        ADoctor doctor = getItem(position);
        TextView doctorName = convertView.findViewById(R.id.doctorName);
        TextView doctorSSN = convertView.findViewById(R.id.doctorSSN);

        doctorName.setText(doctor.getDisplayName());
        doctorSSN.setText(doctor.getSSN());
        return convertView;
    }
}


public class DoctorsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_doctors, container, false);
        ListView listView = rootView.findViewById(R.id.listView);
        ArrayList<ADoctor> doctors = ((AManagerUser)StaticFunctionUtilities.getUser()).getDoctors(false);
        DoctorAdapter adapter = new DoctorAdapter(ContextFlowUtilities.getCurrentView(), doctors);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Σίγουρα θες να αφαιρέσεις αυτόν τον χρήστη;")
                        .setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                            ADoctor doctorToDelete = doctors.get(i);
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(((AManagerUser) StaticFunctionUtilities.getUser()).deleteDoctor(doctorToDelete)){
                                            doctors.remove(doctorToDelete);
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
                                }).start();
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
                return false;
            }
        });
        return rootView;
    }
}