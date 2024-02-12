package com.geoxhonapps.physio_app.RestUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FCreateUserResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAppointmentResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetAvailabilityResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetChildrenResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetCreatorResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetHistoryResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FGetServicesResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FLoginResponse;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FRestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class RestController {
    private String userId;
    private String username;
    private RequestComponent requestComponent;

    public RestController() {
        requestComponent = new RequestComponent();
    }

    public boolean doLogout() throws IOException{
        JSONObject obj = new JSONObject();
        FRestResponse r = requestComponent.Post("/api/v1/auth/logout", obj);
        return r.statusCode == 200 || r.statusCode == 403;
    }
    public FLoginResponse doLogin(String username, String password) throws IOException, JSONException{
        JSONObject obj = new JSONObject();

        obj.put("username", username);
        obj.put("password", password);

        FRestResponse r = requestComponent.Post("/api/v1/auth/login", obj);
        System.out.println(r.responseContent);
        if(r.statusCode==200) {
            JSONObject data = new JSONObject(r.responseContent);
            data = (JSONObject)data.get("triggerResults");
            userId = (String) data.get("userId");
            // Save the refresh token
            SharedPreferences sharedPreferences = ContextFlowUtilities.getCurrentView().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("refresh_token", data.getString("refreshToken"));
            editor.apply();
            return new FLoginResponse(true, userId, (String)data.get("displayName"), username, ((Number)data.get("userType")).intValue(), data.getString("email"), data.getString("ssn"), data.getString("address"));
        }
        return new FLoginResponse(false);
    }
    public FLoginResponse doLoginToken(String refreshToken) throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        FRestResponse r = requestComponent.Post("/api/v1/auth/token/"+refreshToken+"/login", obj);
        if(r.statusCode==200) {
            JSONObject data = new JSONObject(r.responseContent);
            data = (JSONObject)data.get("triggerResults");
            userId = (String) data.get("userId");
            // Save the refresh token
            SharedPreferences sharedPreferences = ContextFlowUtilities.getCurrentView().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("refresh_token", data.getString("refreshToken"));
            editor.apply();
            username = data.getString("username");
            return new FLoginResponse(true, userId, (String)data.get("displayName"), username, ((Number)data.get("userType")).intValue(), data.getString("email"), data.getString("ssn"), data.getString("address"));
        }
        return new FLoginResponse(false);
    }
    public ArrayList<FGetChildrenResponse> getAllChildren() throws IOException, JSONException {
        ArrayList<FGetChildrenResponse> outPatients = new ArrayList<FGetChildrenResponse>();
        FRestResponse r = requestComponent.Get("/api/v1/me/children");
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")){
            data = (JSONObject)data.get("triggerResults");
            JSONArray patients = data.getJSONArray("children");
            for(int i = 0; i<patients.length();i++){
                JSONObject temp = patients.getJSONObject(i);
                outPatients.add(new FGetChildrenResponse(true, temp.getString("id"), temp.getString("displayName"), temp.getString("email"), temp.getString("ssn"), temp.getString("address")));
            }
        }
        System.out.println(outPatients);
        return outPatients;
    }

    public FCreateUserResponse registerUser(String username, String password, String displayName, String email, String SSN, String address) throws JSONException, IOException {
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("password", password);
        obj.put("displayName", displayName);
        obj.put("email", email);
        obj.put("ssn", SSN);
        obj.put("address", address);
        FRestResponse r = requestComponent.Post("/api/v1/auth/register", obj);
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")) {
            data = data.getJSONObject("triggerResults");
            return new FCreateUserResponse(true, data.getString("id"), data.getString("createdAt"), data.getInt("accountType"));
        }
        return new FCreateUserResponse(false);
    }

    public FGetAvailabilityResponse getAvailability() throws JSONException, IOException {
        FRestResponse r = requestComponent.Get("/api/v1/appointments/availability");
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")) {
            JSONArray arr = data.getJSONArray("triggerResults");
            ArrayList<Long> timestamps = new ArrayList<Long>();
            for(int i = 0; i<arr.length(); i++){
                timestamps.add(arr.getLong(i)*1000);
            }
            return new FGetAvailabilityResponse(true, timestamps);
        }
        return new FGetAvailabilityResponse(false);
    }
    public ArrayList<FGetAppointmentResponse> getAppointments() throws JSONException, IOException {
        FRestResponse r = requestComponent.Get("/api/v1/appointments");
        ArrayList<FGetAppointmentResponse> outResults = new ArrayList<FGetAppointmentResponse>();
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")) {
            data = data.getJSONObject("triggerResults");
            JSONArray arr = data.getJSONArray("appointments");
            for(int i = 0; i<arr.length(); i++){
                data = arr.getJSONObject(i);
                outResults.add(new FGetAppointmentResponse(true, data.getInt("id"), data.getString("user"), data.getInt("status"), data.getString("date")));
            }
        }
        return outResults;
    }
    public FGetCreatorResponse getCreator() throws JSONException, IOException{
        FRestResponse r = requestComponent.Get("/api/v1/me/creator");
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")) {
            data = data.getJSONObject("triggerResults");
            data = data.getJSONObject("creator");
            return new FGetCreatorResponse(true, data.getString("displayName"), data.getString("email"), data.getString("ssn"));
        }
        return new FGetCreatorResponse(false);
    }
    public boolean acceptAppointment(int id) throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        FRestResponse r = requestComponent.Post("/api/v1/appointments/"+Integer.toString(id)+"/accept", obj);
        JSONObject data = new JSONObject(r.responseContent);
        return data.getBoolean("success");
    }

    public boolean cancelAppointment(int id) throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        FRestResponse r = requestComponent.Post("/api/v1/appointments/"+Integer.toString(id)+"/cancel", obj);
        JSONObject data = new JSONObject(r.responseContent);
        return data.getBoolean("success");
    }

    public int bookAppointment(Long timestamp) throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        obj.put("timestamp", timestamp);
        FRestResponse r = requestComponent.Post("/api/v1/appointments/book", obj);
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")){
            data = data.getJSONObject("triggerResults");
            return data.getInt("appointmentId");
        }
        return -1;
    }

    public ArrayList<FGetServicesResponse> getServices() throws IOException, JSONException{
        FRestResponse r = requestComponent.Get("/api/v1/services");
        JSONObject data = new JSONObject(r.responseContent);
        ArrayList<FGetServicesResponse> outServices = new ArrayList<FGetServicesResponse>();
        if(data.getBoolean("success")){
            data = data.getJSONObject("triggerResults");
            JSONArray arr = data.getJSONArray("services");
            for(int i = 0; i<arr.length(); i++){
                JSONObject temp = arr.getJSONObject(i);
                outServices.add(new FGetServicesResponse(true, temp.getString("id"), temp.getString("name"), temp.getString("description"), temp.getInt("cost")));
            }
        }
        return outServices;
    }

    public boolean createService(String serviceId, String name, String description, int cost) throws JSONException, IOException {
        JSONObject obj = new JSONObject();
        obj.put("id", serviceId);
        obj.put("name", name);
        obj.put("description", description);
        obj.put("cost", cost);
        FRestResponse r = requestComponent.Post("/api/v1/services/create", obj);
        JSONObject data = new JSONObject(r.responseContent);
        return data.getBoolean("success");
    }

    public ArrayList<FGetHistoryResponse> getHistory() throws JSONException, IOException{
        FRestResponse r = requestComponent.Get("/api/v1/appointments/history");
        ArrayList<FGetHistoryResponse> outResults = new ArrayList<FGetHistoryResponse>();
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")){
            data = data.getJSONObject("triggerResults");
            JSONArray arr = data.getJSONArray("records");
            for(int i = 0; i<arr.length();i++){
                data = arr.getJSONObject(i);
                outResults.add(new FGetHistoryResponse(true, data.getInt("id"), data.getString("doctorId"), data.getString("patientId"),
                        data.getString("details"), data.getString("serviceId"), data.getString("date")));
            }
        }
        return outResults;
    }
    public ArrayList<FGetHistoryResponse> getUserHistory(String userId) throws JSONException, IOException{
        FRestResponse r = requestComponent.Get("/api/v1/user/"+userId+"/history");
        ArrayList<FGetHistoryResponse> outResults = new ArrayList<FGetHistoryResponse>();
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")){
            data = data.getJSONObject("triggerResults");
            JSONArray arr = data.getJSONArray("records");
            for(int i = 0; i<arr.length();i++){
                data = arr.getJSONObject(i);
                outResults.add(new FGetHistoryResponse(true, data.getInt("id"), data.getString("doctorId"), data.getString("patientId"),
                        data.getString("details"), data.getString("serviceId"), data.getString("date")));
            }
        }
        return outResults;
    }

    public int addAppointmentToRecord(int appointmentId, String serviceId, String details) throws JSONException, IOException{
        JSONObject obj = new JSONObject();
        obj.put("serviceId", serviceId);
        obj.put("details", details);
        FRestResponse r = requestComponent.Post("/api/v1/appointments/"+Integer.toString(appointmentId)+"/record", obj);
        JSONObject data = new JSONObject(r.responseContent);
        if(data.getBoolean("success")){
            data = data.getJSONObject("triggerResults");
            return data.getInt("generatedId");
        }
        return -1;

    }

    public boolean deleteUser(String userId) throws IOException, JSONException {
        FRestResponse r = requestComponent.Delete("/api/v1/user/"+userId);
        JSONObject data = new JSONObject(r.responseContent);
        return data.getBoolean("success");
    }
}
