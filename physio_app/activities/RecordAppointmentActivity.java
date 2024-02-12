package com.geoxhonapps.physio_app.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.AAppointment;
import com.geoxhonapps.physio_app.RestUtilities.AManagerUser;
import com.geoxhonapps.physio_app.RestUtilities.AService;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import org.w3c.dom.Text;

public class RecordAppointmentActivity extends ParentActivity {
    private AAppointment selectedAppointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        if(ContextFlowUtilities.getPassedObject()!=null){
            selectedAppointment = (AAppointment)ContextFlowUtilities.getPassedObject();
        }
        setContentView(R.layout.r8);
        ((TextView)findViewById(R.id.appointmentIdText)).setText("#"+selectedAppointment.getAppointmentId());
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        for(AService service: StaticFunctionUtilities.getUser().getServices(false)){
            adapter.add(service.getName());
        }
        Button btn = findViewById(R.id.saveButtonr8);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AService serviceUsed = StaticFunctionUtilities.getUser().getServices(false).get(spinner.getSelectedItemPosition());
                TextView t = (TextView)findViewById(R.id.detailsText);
                String details = t.getText().toString();
                ContextFlowUtilities.presentLoadingAlert("Παρακαλώ Περιμένετε", false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (selectedAppointment.recordAppointment(serviceUsed,details)!=null){
                            ContextFlowUtilities.dismissLoadingAlert();
                            ContextFlowUtilities.presentAlert("Επιτυχία", "Το ραντεβού καταγράφηκε με επιτυχία");
                        }else{
                            ContextFlowUtilities.dismissLoadingAlert();
                            ContextFlowUtilities.presentAlert("Σφάλμα", "Υπήρξε ένα πρόβλημα κατά την καταγραφή του ραντεβού. Παρακαλώ δοκιμάστε ξανά");
                        }
                    }
                }).start();
            }
        });
    }


}