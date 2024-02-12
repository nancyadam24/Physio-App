package com.geoxhonapps.physio_app.activities;

import static com.geoxhonapps.physio_app.R.color.grey_text_hint;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.APatientUser;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewAppointmentActivity extends ParentActivity {
    private CalendarView calendarView;
    private Button button1;
    private String formattedHour;
    private Date thiselectedate;

    private  HorizontalScrollView scrollview;
    private boolean flag;

    private List<String> formattedHours = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r9);
        CalendarView calendarView = findViewById(R.id.calendar);
        getSupportActionBar().hide();
        button1 = findViewById(R.id.btn);
        button1.setEnabled(false);
        button1.setBackgroundColor(getResources().getColor(grey_text_hint));
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                scrollview = findViewById(R.id.scrollView);
                LinearLayout buttonContainer = findViewById(R.id.buttonContainer);
                buttonContainer.removeAllViews();
                formattedHours.clear();

                APatientUser user = (APatientUser) StaticFunctionUtilities.getUser();
                String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                ArrayList<Date> listOfHours = user.getAvailableAppointmentsForDate(selectedDate);

                if (listOfHours.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No available hours. If you want,choose another day.", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");


                    for (Date hour : listOfHours) {
                        formattedHour = hourFormat.format(hour);
                        formattedHours.add(formattedHour);
                    }

                   // HorizontalScrollView scroll= findViewById(R.id.scrollview);

                    for (int i = 0; i < formattedHours.size(); i++) {
                        final String hour = formattedHours.get(i);
                        Button button = new Button(NewAppointmentActivity.this);

                        button.setBackgroundColor(Color.LTGRAY);

                        button.setWidth(50);
                        button.setHeight(20);

                        // set text
                        button.setText(hour);

                        // create patameter
                        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        
                        buttonContainer.addView(button);
                        button.setText(hour);
                        button.setTag(i);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = (int) v.getTag();
                                //selectedHour = formattedHours.get(position);
                                thiselectedate = listOfHours.get(position);
                                button1.setEnabled(true);
                                button1.setBackgroundColor(getResources().getColor(R.color.physio_green));
                            }
                        });

                    }

                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    });
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            button1.setEnabled(false);
                            button1.setBackgroundColor(getResources().getColor(grey_text_hint));
                            ContextFlowUtilities.presentLoadingAlert("Παρακαλώ Περιμένετε", false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (user.bookAppointment(thiselectedate)!=null){
                                        ContextFlowUtilities.dismissLoadingAlert();
                                        ContextFlowUtilities.presentAlert("Επιτυχία", "Το ραντεβού αποθηκεύτηκε με επιτυχία");
                                    }
                                    else{
                                        ContextFlowUtilities.dismissLoadingAlert();
                                        ContextFlowUtilities.presentAlert("Σφάλμα", "Το ραντεβού δεν αποθηκεύτηκε, παρακαλώ προσπαθήστε αργότερα.");
                                    }
                                }
                            }).start();
                        }
                    });
                }

            }
        });
    }
}

