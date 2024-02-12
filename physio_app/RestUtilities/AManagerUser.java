package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FCreateUserResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetChildrenResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetServicesResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FLoginResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.geoxhonapps.physio_app.activities.HomeActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class AManagerUser extends AUser{
    private ArrayList<ADoctor> myDoctors;
    public AManagerUser(FLoginResponse userInfo) {
        super(userInfo);
        myDoctors = new ArrayList<ADoctor>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<FGetChildrenResponse> temp = StaticFunctionUtilities.getRestController().getAllChildren();
                    for(int i =0; i<temp.size();i++){
                        myDoctors.add(new ADoctor(temp.get(i)));
                    }
                    ArrayList<FGetServicesResponse> tempServices = StaticFunctionUtilities.getRestController().getServices();
                    for(int i =0; i<tempServices.size();i++){
                        services.add(new AService(tempServices.get(i)));
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
     * Συνάρτηση που επιστρέφει μια λίστα από τους γιατρούς που έχει δημιουργήσει ο διαχειριστής.
     * @param bShouldRefreshList Αν θα πρέπει να ανανεώσουμε την λίστα κάνοντας αίτημα στο API, αν ναι τότε η συνάρτηση δεν πρέπει να τρέξει από το κύριο thread
     * @return Επιστρέφει λίστα με γιατρούς
     */
    public ArrayList<ADoctor> getDoctors(boolean bShouldRefreshList){
        if(bShouldRefreshList){
            ArrayList<FGetChildrenResponse> temp = null;
            try {
                temp = StaticFunctionUtilities.getRestController().getAllChildren();
                myDoctors.clear();
                for(int i =0; i<temp.size();i++){
                    myDoctors.add(new ADoctor(temp.get(i)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return myDoctors;
    }

    /**
     * Συνάρτηση που δημιουργεί έναν καινούργιο γιατρό/ιατρείο και τον προσθέτει στην βάση δεδομένων.
     * ΝΑ ΜΗΝ ΕΚΤΕΛΕΙΤΑΙ ΑΠΟ ΤΟ ΚΥΡΙΟ THREAD
     * @param username Το όνομα χρήστη του νέου ασθενή
     * @param password Ο κωδικός του νέου ασθενή
     * @param displayName Το Ονοματεπώνυμο του που θα εμφανίζεται και στην λίστα με τους ασθενής
     * @param email Το email του νέου ασθενή
     * @param SSN Το ΑΜΚΑ/ΑΦΜ του.
     * @return Επιστρέφει αν η καταχώρηση ήταν επιτυχής. Αν δεν ηταν επιτυχής πρόκειται για διπλότυπο username ή email.
     */
    public boolean createDoctor(String username, String password, String displayName, String email, String SSN, String address){
        try {
            FCreateUserResponse newPatient = StaticFunctionUtilities.getRestController().registerUser(username, password, displayName, email, SSN, address);
            if(newPatient.isSuccess){
                myDoctors.add(new ADoctor(newPatient.userId, displayName, email, SSN, address));
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
    public boolean deleteDoctor(ADoctor doctor){
        try {
            if(StaticFunctionUtilities.getRestController().deleteUser(doctor.getUserId())){
                myDoctors.remove(doctor);
                return true;
            }
        } catch (IOException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
        return false;
    }
    /**
     * Συνάρτηση που δημιουργεί μια νέα παροχή για τους γιατρούς και την προσθέτει στην βάση δεδομένων.
     * @param id Το id της παροχής, μέχρι 5 χαρακτήρες πχ EX001
     * @param name Το όνομα της παροχής
     * @param description Η περιγραφή της παροχής
     * @param cost Το κόστος της παροχής
     * @return Επιστρέφει αν η καταχώρηση ήταν επιτυχής. Αν δεν ήταν επιτυχής πρόκειται για διπλότυπο id.
     */
    public boolean createService(String id, String name, String description, int cost){
        try {
            boolean isSuccess = StaticFunctionUtilities.getRestController().createService(id, name, description, cost);
            if(isSuccess){
                services.add(new AService(new FGetServicesResponse(true, id, name, description, cost)));
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

}
