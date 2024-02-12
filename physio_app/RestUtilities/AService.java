package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetServicesResponse;

public class AService {
    private String id;
    private String name;
    private String description;
    private int cost;

    public AService(FGetServicesResponse serviceInfo){
        this.id = serviceInfo.id;
        this.name = serviceInfo.name;
        this.description = serviceInfo.description;
        this.cost = serviceInfo.cost;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }
}
