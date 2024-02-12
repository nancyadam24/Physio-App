package com.geoxhonapps.physio_app.RestUtilities;

import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetChildrenResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetHistoryResponse;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class APatient extends AInfo{
    private String userId;
    private String displayName;
    private String email;
    private String SSN;
    private ArrayList<ARecord> patientHistory = new ArrayList<ARecord>();
    public APatient(String userId, String displayName, String email, String SSN, String address){
        super(userId, displayName, email, SSN, address);
    }
    public APatient(FGetChildrenResponse userInfo){
        super(userInfo);
    }

    public boolean addRecord(ARecord newRecord){
        for(ARecord record: patientHistory){
            if(record.getId() == newRecord.getId()){
                return false;
            }
        }
        patientHistory.add(newRecord);
        return true;
    }

    /**
     * Συνάρτηση για την λήψη του ιστορικού του συγκεκριμένου ασθενή
     * @return Επιστρέφει λίστα με records
     */
    public ArrayList<ARecord> getPatientHistory(){
        return this.patientHistory;
    }
}
