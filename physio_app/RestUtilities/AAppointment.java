package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAppointmentResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetHistoryResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AAppointment {
    private Date appointmentDate;
    private EAppointmentStatus status;
    private AInfo associatedUser;
    private int appointmentId;
    public AAppointment(FGetAppointmentResponse appointmentInfo){
        appointmentId = appointmentInfo.id;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            appointmentDate = dateFormat.parse(appointmentInfo.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        status = EAppointmentStatus.values()[appointmentInfo.status];
        if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Doctor){
            ArrayList<APatient> patients = ((ADoctorUser)StaticFunctionUtilities.getUser()).getPatients(false);
            for(int i = 0; i< patients.size(); i++){
                if(patients.get(i).getUserId().equals(appointmentInfo.user)){
                    this.associatedUser = patients.get(i);
                    break;
                }
            }
        }else if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Patient){
            this.associatedUser = ((APatientUser)StaticFunctionUtilities.getUser()).getMyDoctor();
        }
    }
    public AAppointment(int appointmentId, AInfo associatedUser, EAppointmentStatus status, Date appointmentDate){
        this.appointmentId = appointmentId;
        this.associatedUser = associatedUser;
        this.status = status;
        this.appointmentDate = appointmentDate;
    }
    /**
     * Συνάρτηση για την λήψη της ημερομηνίας του ραντεβού
     * @return Επιστρέφει την ημερομηνία σε μορφή Date
     */
    public Date getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Συνάρτηση για την κατάσταση του ραντεβού(Εν αναμονή, Ακυρωμένο κλπ)
     * @return Επιστρέφει την κατάσταση με ENUM
     */
    public EAppointmentStatus getStatus() {
        return status;
    }

    /**
     * Συνάρτηση για την λήψη του συσχετιζόμενου χρήστη.
     * @return Αν ο συνδεδεμένος χρήστης είναι ασθενής τότε επιστρέφει τον γιατρό του αλλιώς αν ο συνδεδεμένος χρήστης είναι γιατρός επιστρέφει έναν ασθενή
     */
    public AInfo getAssociatedUser() {
        return associatedUser;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * Συνάρτηση για την αποδοχή ενός ραντεβού από τον γιατρό
     * Δεν μπορεί να κληθεί αν το ραντεβού έχει ήδη επιβεβαιωθεί ή ακυρωθεί
     * Μόνο γιατρός μπορεί να καλέσει αυτή την συνάρτηση
     * ΝΑ ΜΗΝ ΕΚΤΕΛΕΙΤΕ ΑΠΟ ΤΟ ΚΥΡΙΟ THREAD
     * @return Επιστρέφει αν η αποδοχή την επιτυχής ή όχι
     */
    public boolean Accept(){
        if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Doctor){
            try {
                if(StaticFunctionUtilities.getRestController().acceptAppointment(appointmentId)){
                    status = EAppointmentStatus.Confirmed;
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Συνάρτηση για την ακύρωση ενός ραντεβού
     * Δεν μπορεί να κληθεί αν το ραντεβού είναι ηδη ακυρωμένο
     * Μπορεί να την καλέσουν και οι γιατροί και οι ασθενείς
     * @return Επιστρέφει αν η ακύρωση ήταν επιτυχής
     */
    public boolean Cancel(){
        if(StaticFunctionUtilities.getUser().getAccountType() != EUserType.Manager){
            try {
                if(StaticFunctionUtilities.getRestController().cancelAppointment(appointmentId)){
                    status = EAppointmentStatus.Cancelled;
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Συνάρτηση για την αποθήκευση και καταγραφή ενός ραντεβού στο ιστορικό.
     * ΝΑ ΜΗΝ ΕΚΤΕΛΕΙΤΕ ΑΠΟ ΤΟ ΚΥΡΙΟ THREAD
     * Μόνο γιατροί μπορούν να καλέσουν αυτή την συνάρτηση.
     * @param serviceUsed Η παροχή που δόθηκε στον ασθενή
     * @param details Επιπλέον λεπτομέρειες για το ραντεβού.
     * @return Επιστρέφει το record που δημιουργήθηκε, σε περίπτωση που είναι null υπήρξε πρόβλημα κατά την αποθήκευση.
     */
    public ARecord recordAppointment(AService serviceUsed, String details){
        if(StaticFunctionUtilities.getUser().getAccountType() == EUserType.Doctor){
            int id = 0;
            try {
                id = StaticFunctionUtilities.getRestController().addAppointmentToRecord(this.appointmentId, serviceUsed.getId(), details);
                if(id != -1){
                    this.status = EAppointmentStatus.Completed;
                    return new ARecord(new FGetHistoryResponse(true, id, StaticFunctionUtilities.getUser().getUserId(), associatedUser.getUserId(),
                            details, serviceUsed.getId(), getGlobalDateString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public String getGlobalDateString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(appointmentDate);
    }
}
