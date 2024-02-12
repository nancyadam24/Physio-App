package com.geoxhonapps.physio_app.RestUtilities.Responses;

import java.util.ArrayList;

public class FGetAvailabilityResponse {
    public boolean isSuccess;
    public ArrayList<Long> bookedTimestamps;
    public FGetAvailabilityResponse(boolean success) {
        isSuccess = success;
    }

    public FGetAvailabilityResponse(boolean success, ArrayList<Long> bookedTimestamps) {
        isSuccess = success;
        this.bookedTimestamps = bookedTimestamps;
    }
}
