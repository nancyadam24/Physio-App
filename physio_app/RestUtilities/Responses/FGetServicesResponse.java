package com.geoxhonapps.physio_app.RestUtilities.Responses;

public class FGetServicesResponse {
    public boolean isSuccess;
    public String id;
    public String name;
    public String description;
    public int cost;
    public FGetServicesResponse(boolean success) {
        isSuccess = success;
    }

    public FGetServicesResponse(boolean success, String id, String name, String description, int cost) {
        isSuccess = success;
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
    }
}
