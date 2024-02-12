package com.geoxhonapps.physio_app.RestUtilities.Responses;

public class FGetAppointmentResponse {
    public boolean isSuccess;
    public String date;
    public int status;
    public String user;
    public int id;
    public FGetAppointmentResponse(boolean success) {
        isSuccess = success;
    }

    public FGetAppointmentResponse(boolean success, int id, String user, int status, String date){
        this.isSuccess = success;
        this.id = id;
        this.user = user;
        this.status = status;
        this.date = date;
    }
}
