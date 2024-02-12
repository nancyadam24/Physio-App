package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAppointmentResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAvailabilityResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetCreatorResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetHistoryResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetServicesResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FLoginResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.geoxhonapps.physio_app.activities.HomeActivity;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class APatientUser extends AUser{
    private ArrayList<Long> bookedTimestamps;
    private ADoctor myDoctor;
    private ArrayList<AAppointment> myAppointments = new ArrayList<AAppointment>();
    private ArrayList<ARecord>  history;
    public APatientUser(FLoginResponse userInfo) {
        super(userInfo);
        bookedTimestamps = new ArrayList<Long>();
        history = new ArrayList<ARecord>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FGetAvailabilityResponse response = StaticFunctionUtilities.getRestController().getAvailability();
                    if(response.isSuccess){
                        bookedTimestamps =  response.bookedTimestamps;
                    }
                    FGetCreatorResponse creator = StaticFunctionUtilities.getRestController().getCreator();
                    myDoctor = new ADoctor("", creator.displayName, creator.email, creator.SSN, "");
                    ArrayList<FGetAppointmentResponse> tempAppointments = StaticFunctionUtilities.getRestController().getAppointments();
                    for(int i = 0; i<tempAppointments.size(); i++){
                        myAppointments.add(new AAppointment(tempAppointments.get(i)));
                    }
                    ArrayList<FGetServicesResponse> temp = StaticFunctionUtilities.getRestController().getServices();
                    for(int i =0; i<temp.size();i++){
                        services.add(new AService(temp.get(i)));
                    }
                    ArrayList<FGetHistoryResponse> tempHistory = StaticFunctionUtilities.getRestController().getHistory();
                    for(FGetHistoryResponse historyResponse: tempHistory){
                        history.add(new ARecord(historyResponse));
                    }
                    ContextFlowUtilities.moveTo(HomeActivity.class, false);
                } catch (IOException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Η σύνδεση δεν ήταν επιτυχής, παρακαλώ προσπαθήστε ξανά");
                } catch (JSONException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Η σύνδεση δεν ήταν επιτυχής, παρακαλώ προσπαθήστε ξανά");
                }
            }
        }).start();
    }

    /**
     * Συνάρτηση για την λήψη του αμέσως επόμενου ραντεβού
     * @return Επιστρέφει το ραντεβού, αν δεν υπάρχει επιστρέφει null.
     */
    public AAppointment getNextAppointment(){
        AAppointment outAppointment = null;
        Date currentDate = new Date();
        for(AAppointment appointment: myAppointments){
            if(appointment.getStatus() == EAppointmentStatus.Confirmed && currentDate.getTime()<appointment.getAppointmentDate().getTime()){
                if(outAppointment == null){
                    outAppointment = appointment;
                }else if(appointment.getAppointmentDate().getTime()<appointment.getAppointmentDate().getTime()){
                    outAppointment = appointment;
                }
            }
        }
        return outAppointment;
    }
    /**
     * Συνάρτηση που επιστρέφει μια λίστα με τα ραντεβού του γιατρού
     * @param bShouldRefreshList Αν θα πρέπει να ανανεώσουμε την λίστα κάνοντας αίτημα στο API, αν ναι τότε η συνάρτηση δεν πρέπει να τρέξει από το κύριο thread
     * @return Επιστρέφει λίστα με ραντεβού
     */
    public ArrayList<AAppointment> getAppointments(boolean bShouldRefreshList){
        if(bShouldRefreshList){
            ArrayList<FGetAppointmentResponse> tempAppointments = null;
            try {
                tempAppointments = StaticFunctionUtilities.getRestController().getAppointments();
                myAppointments.clear();
                for(int i = 0; i<tempAppointments.size(); i++){
                    myAppointments.add(new AAppointment(tempAppointments.get(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myAppointments;
    }

    /**
     * Συνάρτηση για την λήψη διαθέσιμων ραντεβού για μια συγκεκριμένη ημερομηνία
     *
     * @param date Η Ημερομηνία σε String σε μορφη εεεε-ΜΜ-μμ
     * @return Μια λίστα με διαθέσιμες ώρες σε μορφή date.
     */
    public ArrayList<Date> getAvailableAppointmentsForDate(String date){
        ArrayList<Date> outResults = new ArrayList<Date>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            for(int i = 10; i<22; i++){
                Date parsedDate = dateFormat.parse(date+" "+ Integer.toString(i)+":00:00");
                if(!bookedTimestamps.contains(parsedDate.getTime())){
                    outResults.add(parsedDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outResults;
    }

    /**
     * Συνάρτηση για την δέσμευση ραντεβού με τον γιατρό
     * ΝΑ ΜΗΝ ΕΚΤΕΛΕΙΤΕ ΑΠΟ ΤΟ ΚΥΡΙΟ THREAD.
     * @param date Η Ημερομηνία του ραντεβού
     * @return Επιστρέφει αν η καταχώρηση ήταν επιτυχής
     */
    public AAppointment bookAppointment(Date date){
        try {
            int newAppointmentId = StaticFunctionUtilities.getRestController().bookAppointment(date.getTime()/1000);
            if(newAppointmentId!=-1){
                AAppointment newAppointment = new AAppointment(newAppointmentId, myDoctor, EAppointmentStatus.Pending, date);
                ArrayList<AAppointment> newList = new ArrayList<AAppointment>();
                newList.add(newAppointment);
                for(AAppointment oldAppointment: myAppointments){
                    newList.add(oldAppointment);
                }
                bookedTimestamps.add(date.getTime());
                myAppointments = newList;
                return newAppointment;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ADoctor getMyDoctor(){
        return myDoctor;
    }

    public ArrayList<ARecord> getHistory(){
        return this.history;
    }
}
