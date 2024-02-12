package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FCreateUserResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAppointmentResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetChildrenResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetHistoryResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetServicesResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FLoginResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.geoxhonapps.physio_app.activities.HomeActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ADoctorUser extends AUser{
    private ArrayList<APatient> myPatients;
    private ArrayList<AAppointment> myAppointments;
    private ArrayList<ARecord>  history;
    public ADoctorUser(FLoginResponse userInfo) {
        super(userInfo);
        myPatients = new ArrayList<APatient>();
        myAppointments = new ArrayList<AAppointment>();
        history = new ArrayList<ARecord>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<FGetChildrenResponse> temp = StaticFunctionUtilities.getRestController().getAllChildren();
                    for(int i =0; i<temp.size();i++){
                        myPatients.add(new APatient(temp.get(i)));
                    }
                    ArrayList<FGetAppointmentResponse> tempAppointments = StaticFunctionUtilities.getRestController().getAppointments();
                    for(int i = 0; i<tempAppointments.size(); i++){
                        myAppointments.add(new AAppointment(tempAppointments.get(i)));
                    }
                    ArrayList<FGetServicesResponse> tempServices = StaticFunctionUtilities.getRestController().getServices();
                    for(int i =0; i<tempServices.size();i++){
                        services.add(new AService(tempServices.get(i)));
                    }
                    ArrayList<FGetHistoryResponse> tempHistory = StaticFunctionUtilities.getRestController().getHistory();
                    history = new ArrayList<ARecord>();
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
                }else if(appointment.getAppointmentDate().getTime()<outAppointment.getAppointmentDate().getTime()){
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
     * Συνάρτηση που επιστρέφει μια λίστα από τους ασθενείς που έχει δηλώσει ο γιατρός.
     * @param bShouldRefreshList Αν θα πρέπει να ανανεώσουμε την λίστα κάνοντας αίτημα στο API, αν ναι τότε η συνάρτηση δεν πρέπει να τρέξει από το κύριο thread
     * @return Επιστρέφει λίστα με ασθενείς
     */
    public ArrayList<APatient> getPatients(boolean bShouldRefreshList){
        if(bShouldRefreshList){
            ArrayList<FGetChildrenResponse> temp = null;
            try {
                temp = StaticFunctionUtilities.getRestController().getAllChildren();
                myPatients.clear();
                for(int i =0; i<temp.size();i++){
                    myPatients.add(new APatient(temp.get(i)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return myPatients;
    }

    /**
     * Συνάρτηση που δημιουργεί έναν καινούργιο ασθενή και τον προσθέτει στην βάση δεδομένων.
     * ΝΑ ΜΗΝ ΕΚΤΕΛΕΙΤΑΙ ΑΠΟ ΤΟ ΚΥΡΙΟ THREAD
     * @param username Το όνομα χρήστη του νέου ασθενή
     * @param password Ο κωδικός του νέου ασθενή
     * @param displayName Το Ονοματεπώνυμο του που θα εμφανίζεται και στην λίστα με τους ασθενής
     * @param email Το email του νέου ασθενή
     * @param SSN Το ΑΜΚΑ/ΑΦΜ του.
     * @return Επιστρέφει αν η καταχώρηση ήταν επιτυχής. Αν δεν ηταν επιτυχής πρόκειται για διπλότυπο username ή email.
     */
    public boolean createPatient(String username, String password, String displayName, String email, String SSN, String address){
        try {
            FCreateUserResponse newPatient = StaticFunctionUtilities.getRestController().registerUser(username, password, displayName, email, SSN, address);
            if(newPatient.isSuccess){
                myPatients.add(new APatient(newPatient.userId, displayName, email, SSN, address));
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public APatient GetPatientById(String id){
        for(APatient patient: myPatients){
            if(patient.getUserId().equals(id)){
                return patient;
            }
        }
        return null;
    }

    /**
     * Συνάρτηση για την λήψη του ιστορικού του γιατρού
     * @return Επιστρέφει μια λίστα με records.
     */
    public ArrayList<ARecord> getHistory(){
        return this.history;
    }
}
