package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetHistoryResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ARecord {
    private int id;
    private APatient patient;
    private ADoctor doctor;
    private String recordDetails;
    private AService service;
    private Date date;
    public ARecord(FGetHistoryResponse recordInfo){
        this.id = recordInfo.id;
        this.recordDetails = recordInfo.details;
        this.service = StaticFunctionUtilities.getUser().getServiceById(recordInfo.service);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.date = dateFormat.parse(recordInfo.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AUser currentUser = StaticFunctionUtilities.getUser();
        if(currentUser.getAccountType() == EUserType.Doctor){
            this.doctor = new ADoctor(currentUser.getUserId(), currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getSSN(), currentUser.getAddress());
            this.patient = ((ADoctorUser)currentUser).GetPatientById(recordInfo.patient);
            this.patient.addRecord(this);
        }else if(currentUser.getAccountType() == EUserType.Patient){
            this.doctor = ((APatientUser)currentUser).getMyDoctor();
            this.patient = new APatient(currentUser.getUserId(), currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getSSN(), currentUser.getAddress());
        }
    }

    public APatient getPatient() {
        return patient;
    }

    public ADoctor getDoctor() {
        return doctor;
    }

    public String getRecordDetails() {
        return recordDetails;
    }

    public AService getService() {
        return service;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getGlobalDateString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
