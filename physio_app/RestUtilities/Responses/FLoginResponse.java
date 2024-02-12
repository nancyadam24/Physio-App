package com.geoxhonapps.physio_app.RestUtilities.Responses;



public class FLoginResponse{
    public boolean isSuccess;
    public String userId;
    public String displayName;
    public String username;
    public int accountType;
    public String email;
    public String SSN;
    public String address;
    public FLoginResponse(boolean success) {
        isSuccess = success;
    }

    public FLoginResponse(boolean success, String id, String Name, String username, int accountType, String email, String SSN, String address) {
        isSuccess = success;
        userId = id;
        displayName = Name;
        this.username = username;
        this.accountType = accountType;
        this.email = email;
        this.SSN = SSN;
        this.address = address;
    }


}
