package com.geoxhonapps.physio_app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPatientActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r3);
        Button B=findViewById(R.id.buttonr3);
        getSupportActionBar().hide();
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText username = findViewById(R.id.usernamer3);
                String a1 = username.getText().toString();
                EditText password = findViewById(R.id.passwordr3);
                String a2 = password.getText().toString();
                EditText name = findViewById(R.id.namer3);
                String a3 = name.getText().toString();
                EditText email = findViewById(R.id.emailr3);
                String a4 = email.getText().toString();
                EditText amka = findViewById(R.id.amkar3);
                String a5 = amka.getText().toString();
                EditText address = findViewById(R.id.addressr3);
                String a6 = address.getText().toString();
                Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
                Matcher matcher = pattern.matcher(a2);
                if ((a1.equals("")||a2.equals("")||a3.equals("")||a4.equals("")||a5.equals("")||a6.equals(""))){
                    Toast.makeText(getApplicationContext(),"Παρακαλώ συμπληρώστε όλες τις πληροφορίες",Toast.LENGTH_LONG).show();
                } else if (a2.length()<8||!matcher.matches()) {
                    Toast.makeText(getApplicationContext(),"O κωδικός πρέπει να αποτελείται από τουλάχιστον 8 χαρακτήρες, 1 συμβολο, 1 αριθμό και 1 κεφαλαίο χαρακτήρα.",Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean flag = ((ADoctorUser) StaticFunctionUtilities.getUser()).createPatient(a1, a2, a3, a4, a5, a6);
                            if (flag) {
                                username.setHint(" Username");
                                username.setText("");
                                password.setText(" Password");
                                password.setText("");
                                name.setText(" Ονοματεπώνυμο");
                                name.setText("");
                                email.setText(" E-mail");
                                email.setText("");
                                amka.setHint(" ΑΜΚΑ");
                                amka.setText("");
                                ContextFlowUtilities.presentAlert("Επιτυχία", "Ο ασθενής δημηιουργήθηκε με επιτυχία");
                            } else {
                                ContextFlowUtilities.presentAlert("Αποτυχία", "Παρακαλώ επιλέξτε άλλο Username/Email και σιγουρευτείτε πως ο κωδικός σας περιέχει τουλάχιστον έναν αριθμό, πεζά και κεφαλαία γράμματα και ειδικούς χαρακτήρες");
                            }

                        }
                    }).start();
                }

            }
        });

    }

}