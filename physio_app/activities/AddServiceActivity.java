package com.geoxhonapps.physio_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.AManagerUser;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAppointmentResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import java.util.ArrayList;

public class AddServiceActivity extends ParentActivity {

    public Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r2);
        getSupportActionBar().hide();
        btn=findViewById(R.id.r2_save_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText codetext= findViewById(R.id.r2_code_tv);
                String c1=codetext.getText().toString();
                EditText nametext= findViewById(R.id.r2_sname_tv);
                String c2=nametext.getText().toString();
                EditText desctext= findViewById(R.id.r2_desc_tv);
                String c3=desctext.getText().toString();
                EditText costtext= findViewById(R.id.r2_cost_tv);
                String c4=costtext.getText().toString();
                int costasnum;
                if (!c4.equals("")){
                    costasnum=Integer.valueOf(c4);
                }else costasnum=0;


                if (c1.equals("")||c2.equals("")||c3.equals("")||c4.equals("")){
                    Toast.makeText(getApplicationContext(),"Παρακαλώ συμπληρώστε όλες τις πληροφορίες",Toast.LENGTH_LONG).show();
                } else if (costasnum<0) {
                    Toast.makeText(getApplicationContext(),"H τιμή της υπηρεσίας δεν μπορεί να είναι αρνητική",Toast.LENGTH_LONG).show();
                } else if (c1.length()!=5) {
                    Toast.makeText(getApplicationContext(),"Ο κωδικός της υπηρεσίας πρέπει υποχρεωτικά να αποτελείται από 5 χαρακτήρες",Toast.LENGTH_LONG).show();
                } else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean flag=((AManagerUser) StaticFunctionUtilities.getUser()).createService(c1,c2,c3,costasnum);
                                if (flag){
                                    codetext.setHint("Κωδικός");
                                    codetext.setText("");
                                    nametext.setText("Όνομα Παροχής");
                                    nametext.setText("");
                                    desctext.setText("Περιγραφή");
                                    desctext.setText("");
                                    costtext.setText("Κόστος ανά Συνεδρία");
                                    costtext.setText("");
                                    ContextFlowUtilities.presentAlert("Επιτυχία","Η υπηρεσία δημηιουργήθηκε με επιτυχία");
                                }else {
                                    ContextFlowUtilities.presentAlert("Αποτυχία","Δεν μπορούν δύο υπηρεσίες να έχουν τον ίδιο κωδικό");
                                }
                            }
                        }).start();
                }

            }
        });
    }


}