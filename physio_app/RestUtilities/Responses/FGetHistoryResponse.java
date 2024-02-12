package com.geoxhonapps.physio_app.RestUtilities.Responses;

public class FGetHistoryResponse {
    public boolean isSuccess;
    public String date;
    public int status;
    public String doctor;
    public String patient;
    public int id;
    public String service;
    public String details;
    public FGetHistoryResponse(boolean success) {
        isSuccess = success;
    }

    public FGetHistoryResponse(boolean success, int id, String doctor, String patient, String details, String service, String date){
        this.isSuccess = success;
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.details = details;
        this.service = service;
        this.date = date;
    }
}
