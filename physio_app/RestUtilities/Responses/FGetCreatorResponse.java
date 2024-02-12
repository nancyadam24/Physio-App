package com.geoxhonapps.physio_app.RestUtilities.Responses;

public class FGetCreatorResponse {
    public boolean isSuccess;
    public String displayName;
    public String email;
    public String SSN;

    public FGetCreatorResponse(boolean success) {
        isSuccess = success;
    }

    public FGetCreatorResponse(boolean success, String Name, String email, String SSN) {
        isSuccess = success;
        displayName = Name;
        this.email = email;
        this.SSN = SSN;
    }
}
