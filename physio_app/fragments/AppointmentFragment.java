package com.geoxhonapps.physio_app.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.RestUtilities.AAppointment;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.RestUtilities.APatientUser;
import com.geoxhonapps.physio_app.RestUtilities.AUser;
import com.geoxhonapps.physio_app.RestUtilities.EAppointmentStatus;
import com.geoxhonapps.physio_app.activities.NewAppointmentActivity;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.EUserType;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.geoxhonapps.physio_app.activities.RecordAppointmentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

class AppointmentViewHandler {
    private AAppointment appointment;
    private View view;
    private Button confirmButton;
    private Button cancelButton;
    private Button recordButton;
    private TextView appointmentStatusText;
    private CardView statusCardView;
    public AppointmentViewHandler(AAppointment appointment, View view){
        this.appointment = appointment;
        this.view = view;
        this.view.setElevation(20);
        //Φόρτωσε όλα τα στοιχεία απο το δοσμένο view
        ((TextView)this.view.findViewById(R.id.appointmentIdText)).setText("#"+appointment.getAppointmentId());
        ((TextView)this.view.findViewById(R.id.patientName)).setText(appointment.getAssociatedUser().getDisplayName());
        ((TextView)this.view.findViewById(R.id.dateText)).setText(appointment.getGlobalDateString());
        this.confirmButton = this.view.findViewById(R.id.confirmButton);
        this.cancelButton = this.view.findViewById(R.id.cancelButton);
        this.recordButton = this.view.findViewById(R.id.recordButton);
        this.appointmentStatusText = this.view.findViewById(R.id.appointmentStatus);
        this.statusCardView = this.view.findViewById(R.id.statusCardView);
        prepareView();
        confirmButton.setOnClickListener(buttonHandler);
        cancelButton.setOnClickListener(buttonHandler);
        recordButton.setOnClickListener(buttonHandler);

    }

    public View getView() {
        return view;
    }

    /**
     * Σύναρτηση που διαμορφώνει το view ανάλογα με το status του ραντεβού
     */
    public void prepareView(){
        if(appointment.getStatus() == EAppointmentStatus.Confirmed){
            //Αν το ραντεβού είναι confirmed, κρύψε το confirm button
            confirmButton.setVisibility(View.GONE);
            //Αν ο χρήστης είναι γιατρός, δείξε το record button.
            if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Doctor){
                recordButton.setVisibility(View.VISIBLE);
            }else{
                recordButton.setVisibility(View.GONE);
            }
            appointmentStatusText.setText("Eνεργό");
            statusCardView.setBackgroundColor(Color.parseColor("#56FFB8"));
        }else if(appointment.getStatus() == EAppointmentStatus.Pending){
            //Αν το ραντεβού είναι pending, δείξε το confirm button εφόσον ο χρήστης είναι γιατρός
            if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Doctor){
                confirmButton.setVisibility(View.VISIBLE);
            }else{
                confirmButton.setVisibility(View.GONE);
            }
            appointmentStatusText.setText("Eν Αναμονή");
            statusCardView.setBackgroundColor(Color.parseColor("#FFDA56"));
        }else if(appointment.getStatus() == EAppointmentStatus.Cancelled){
            //Αν το ραντεβού είναι cancelled, κρύψε όλα τα κουμπιά
            confirmButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            recordButton.setVisibility(View.GONE);
            appointmentStatusText.setText("Ακυρωμένο");
            statusCardView.setBackgroundColor(Color.parseColor("#FF5656"));
        }
        view.invalidate();
    }
    public void ASYNC_prepareView(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                prepareView();

            }
        });
    }
    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener buttonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId() ) {
                case R.id.confirmButton:
                    ContextFlowUtilities.presentLoadingAlert("Παρακαλώ Περιμένετε", false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(appointment.Accept()) {
                                ASYNC_prepareView();

                            }
                            ContextFlowUtilities.dismissLoadingAlert();
                        }
                    }).start();
                    break;
                case R.id.cancelButton:
                    ContextFlowUtilities.presentLoadingAlert("Παρακαλώ Περιμένετε", false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(appointment.Cancel()) {
                                ASYNC_prepareView();
                            }
                            ContextFlowUtilities.dismissLoadingAlert();
                        }
                    }).start();
                    break;
                case R.id.recordButton:
                    ContextFlowUtilities.moveTo(RecordAppointmentActivity.class, true, appointment);
                    break;
                default:
                    break;
            }
        }
    };

}

public class AppointmentFragment extends Fragment {
    private String searchString = "";
    private View rootView;
    private TextView allText;
    private TextView pendingText;
    private TextView confirmedText;
    private EAppointmentStatus statusToFilter = EAppointmentStatus.MAX;
    private ArrayList<AppointmentViewHandler> appointmentViewHandlers = new ArrayList<AppointmentViewHandler>();
    public AppointmentFragment() {
        // Required empty public constructor

    }
    @Override
    public void onResume() {
        super.onResume();
        populateScrollView(searchString, statusToFilter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.r7, container, false);
        populateScrollView("", statusToFilter);
        FloatingActionButton bottomButton = rootView.findViewById(R.id.floatingActionButton);
        if(StaticFunctionUtilities.getUser().getAccountType()!=EUserType.Patient){
            bottomButton.hide();
        }
        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(AAppointment appointment: ((APatientUser)StaticFunctionUtilities.getUser()).getAppointments(false)){
                    if(appointment.getStatus()==EAppointmentStatus.Pending){
                        ContextFlowUtilities.presentAlert("Σφάλμα", "Δεν μπορείτε να κλείσετε ραντεβού αν υπάρχει ένα σε αναμονή. Δοκιμάστε να το ακυρώσετε και προσπαθήστε ξανά.");
                        return;
                    }
                }
                ContextFlowUtilities.moveTo(NewAppointmentActivity.class, true);
            }
        });
        allText = rootView.findViewById(R.id.allText);
        pendingText = rootView.findViewById(R.id.pendingText);
        confirmedText = rootView.findViewById(R.id.confirmedText);
        allText.setOnClickListener(sortButtonHandler);
        pendingText.setOnClickListener(sortButtonHandler);
        confirmedText.setOnClickListener(sortButtonHandler);
        EditText searchBar = rootView.findViewById(R.id.search);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchString = editable.toString();
                populateScrollView(searchString, statusToFilter);
            }
        });
        SwipeRefreshLayout pullRefresh = rootView.findViewById(R.id.swiperefresh);
        pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AUser user = StaticFunctionUtilities.getUser();
                        if(user.getAccountType()==EUserType.Doctor){
                            ((ADoctorUser)user).getAppointments(true);
                        }else{
                            ((APatientUser)user).getAppointments(true);
                        }
                        ASYNC_populateScrollView(searchString, statusToFilter);
                        pullRefresh.setRefreshing(false);
                    }
                }).start();
            }
        });
        return rootView;
    }
    public void ASYNC_populateScrollView(String search, EAppointmentStatus filter){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                populateScrollView(search, filter);

            }
        });
    }

    /**
     * Συνάρτηση για την φόρτωση των ραντεβού δυναμικά στο scroll view
     * @param search Αναζήτηση με βάση το όνομα του ασθενή
     */
    public void populateScrollView(String search, EAppointmentStatus statusFilter){
        LinearLayout linearLayout = rootView.findViewById(R.id.appointmentLinearLayout);
        linearLayout.removeAllViews();//Καθάρισε προηγούμενα view
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ArrayList<AAppointment> appointments = new ArrayList<AAppointment>();
        //Πάρε την λίστα με τα ραντεβού
        AUser user = StaticFunctionUtilities.getUser();
        if(user.getAccountType()==EUserType.Doctor){
            appointments = ((ADoctorUser)user).getAppointments(false);
        }else{
            appointments = ((APatientUser)user).getAppointments(false);
        }
        LayoutInflater scrollInflater = null;
        scrollInflater = (LayoutInflater) ContextFlowUtilities.getCurrentView().getLayoutInflater();
        //Για κάθε ραντεβού
        for(AAppointment appointment: appointments){
            //Αν χρησιμοποιειται η αναζήτηση.
            if((!search.isEmpty() && !appointment.getAssociatedUser().getDisplayName().contains(search)) || (statusFilter != EAppointmentStatus.MAX && statusFilter != appointment.getStatus())){
                continue;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                //Δημιουργεία του νέου view
                View view = scrollInflater.inflate(R.layout.r7_card_layout, null);
                view.setPadding(0,0,0, 20);
                linearLayout.addView(view);
                //Δίνουμε το view σε ένα handler για την διαχειρηση του
                appointmentViewHandlers.add(new AppointmentViewHandler(appointment, view));
            }
        }
        rootView.invalidate();
    }
    private View.OnClickListener sortButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId() ) {
                case R.id.allText:
                    v.setBackgroundColor(getResources().getColor(R.color.physio_green));
                    pendingText.setBackgroundColor(Color.parseColor("#ffffff"));
                    confirmedText.setBackgroundColor(Color.parseColor("#ffffff"));
                    statusToFilter = EAppointmentStatus.MAX;
                    break;
                case R.id.pendingText:
                    v.setBackgroundColor(getResources().getColor(R.color.physio_green));
                    allText.setBackgroundColor(Color.parseColor("#ffffff"));
                    confirmedText.setBackgroundColor(Color.parseColor("#ffffff"));
                    statusToFilter = EAppointmentStatus.Pending;
                    break;
                case R.id.confirmedText:
                    statusToFilter = EAppointmentStatus.Confirmed;
                    v.setBackgroundColor(getResources().getColor(R.color.physio_green));
                    pendingText.setBackgroundColor(Color.parseColor("#ffffff"));
                    allText.setBackgroundColor(Color.parseColor("#ffffff"));

                    break;
                default:
                    break;
            }
            populateScrollView(searchString, statusToFilter);
        }
    };
}