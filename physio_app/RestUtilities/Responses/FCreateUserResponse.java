package com.geoxhonapps.physio_app.RestUtilities.Responses;

public class FCreateUserResponse {
    public boolean isSuccess;
    public String userId;
    public String created_at;
    public int accountType;
    public FCreateUserResponse(boolean success) {
        isSuccess = success;
    }

    public FCreateUserResponse(boolean success, String id, String created_at, int accountType) {
        isSuccess = success;
        userId = id;
        this.created_at = created_at;
        this.accountType = accountType;
    }
}
