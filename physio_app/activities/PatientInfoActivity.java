package com.geoxhonapps.physio_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.ADoctor;
import com.geoxhonapps.physio_app.RestUtilities.APatient;
import com.geoxhonapps.physio_app.RestUtilities.ARecord;

import java.util.ArrayList;

class PatientRecordAdapter extends ArrayAdapter<ARecord> {
    public PatientRecordAdapter(Context context, ArrayList<ARecord> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.r4_card_layout, parent, false);
        }
        ARecord record = getItem(position);
        TextView recordId = convertView.findViewById(R.id.appointmentIdText);
        TextView costText = convertView.findViewById(R.id.appointmentCost);
        TextView serviceText = convertView.findViewById(R.id.serviceText);
        TextView date = convertView.findViewById(R.id.dateText);
        TextView details = convertView.findViewById(R.id.appointmentDetailsText);
        recordId.setText("#"+record.getId());
        costText.setText(record.getService().getCost()+"$");
        serviceText.setText(record.getService().getName());
        date.setText(record.getGlobalDateString());
        details.setText(record.getRecordDetails());
        return convertView;
    }
}
public class PatientInfoActivity extends ParentActivity {
    private APatient selectedPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r4);
        getSupportActionBar().hide();
        this.selectedPatient = (APatient) ContextFlowUtilities.getPassedObject();
        TextView userId = findViewById(R.id.username);
        userId.setText(selectedPatient.getUserId());
        ((TextView)findViewById(R.id.displayName)).setText(selectedPatient.getDisplayName());
        ((TextView)findViewById(R.id.email)).setText(selectedPatient.getEmail());
        ((TextView)findViewById(R.id.SSN)).setText(selectedPatient.getSSN());
        ((TextView)findViewById(R.id.addressText)).setText(selectedPatient.getAddress());
        LinearLayout recordLayout = findViewById(R.id.patientRecords);
        ArrayList<ARecord> records = selectedPatient.getPatientHistory();
        PatientRecordAdapter adapter = new PatientRecordAdapter(this, records);
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, recordLayout);
            view.setPadding(0,0,0, 20);
            recordLayout.addView(view);
        }
    }
}