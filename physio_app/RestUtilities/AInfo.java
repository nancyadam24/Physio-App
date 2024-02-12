package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetChildrenResponse;

public class AInfo {
    private String userId;
    private String displayName;
    private String email;
    private String SSN;
    private String address;
    public AInfo(String userId, String displayName, String email, String SSN, String address){
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.SSN = SSN;
        this.address = address;
    }
    public AInfo(FGetChildrenResponse userInfo){
        userId = userInfo.userId;
        displayName = userInfo.displayName;
        email = userInfo.email;
        SSN = userInfo.SSN;
        this.address = userInfo.address;
    }

    /**
     * Πάρε το μοναδικό id του χρήστη
     * @return Επιστρέφει ένα UUID σε String
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Πάρε το όνομα του χρήστη
     * @return Επιστρέφει το όνομα σε String
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * Πάρε το email του χρήστη
     * @return Επιστρέφει το όνομα σε email
     */
    public String getEmail() {
        return email;
    }
    /**
     * Πάρε το ΑΜΚΑ/ΑΦΜ του χρήστη
     * @return Επιστρέφει το AMKA/AΦΜ σε String
     */
    public String getSSN() {
        return SSN;
    }

    public boolean deleteUser(){
        return true;
    }

    public String getAddress() {
        return address;
    }
}
