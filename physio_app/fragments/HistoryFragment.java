package com.geoxhonapps.physio_app.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.RestUtilities.APatientUser;
import com.geoxhonapps.physio_app.RestUtilities.ARecord;
import com.geoxhonapps.physio_app.RestUtilities.AService;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.geoxhonapps.physio_app.activities.AddServiceActivity;
import com.geoxhonapps.physio_app.activities.HomeActivity;
import com.geoxhonapps.physio_app.activities.MainActivity;
import com.geoxhonapps.physio_app.activities.ParentActivity;

import com.geoxhonapps.physio_app.activities.RecordAppointmentActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * create an instance of this fragment.
 */
class HistoryAdapter extends ArrayAdapter<ARecord> {
    public HistoryAdapter(Context context, ArrayList<ARecord> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.r10_card_layout, parent, false);
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
public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.r10, container, false);
        // Inflate the layout for this fragment
        ArrayList<ARecord> records  = ((APatientUser) StaticFunctionUtilities.getUser()).getHistory();
        PieChart pieChart = (PieChart) rootView.findViewById(R.id.piechart);
        HashMap<AService, Integer> spendingByService = new HashMap<AService, Integer>();
        for(ARecord record: records){
            AService temp = record.getService();
            if(spendingByService.containsKey(record.getService())){
                spendingByService.put(temp, spendingByService.get(temp)+ temp.getCost());
            }else{
                spendingByService.put(temp, temp.getCost());
            }
        }
        ArrayList<AService> services = new ArrayList<AService>(spendingByService.keySet());
        Random random = new Random();
        int totalSum = 0;
        for(AService service: services){
            totalSum = totalSum + spendingByService.get(service);
            pieChart.addPieSlice(new PieModel(service.getName(), spendingByService.get(service), Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))));
        }
        ((TextView)rootView.findViewById(R.id.sumText)).setText("Σύνολο: "+totalSum+"€");
        LinearLayout recordLayout = rootView.findViewById(R.id.recordLayout);
        HistoryAdapter adapter = new HistoryAdapter(ContextFlowUtilities.getCurrentView(), records);
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, recordLayout);
            view.setPadding(0,0,0, 20);
            recordLayout.addView(view);
        }
        return rootView;
    }
}