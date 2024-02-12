package com.geoxhonapps.physio_app.RestUtilities.Responses;

public class FGetChildrenResponse {
    public boolean isSuccess;
    public String userId;
    public String displayName;
    public String email;
    public String SSN;
    public String address;
    public FGetChildrenResponse(boolean success) {
        isSuccess = success;
    }

    public FGetChildrenResponse(boolean success, String id, String Name, String email, String SSN, String address) {
        isSuccess = success;
        userId = id;
        displayName = Name;
        this.email = email;
        this.SSN = SSN;
        this.address = address;
    }
}
