package com.geoxhonapps.physio_app;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.RestUtilities.AManagerUser;
import com.geoxhonapps.physio_app.RestUtilities.APatientUser;
import com.geoxhonapps.physio_app.RestUtilities.AUser;
import com.geoxhonapps.physio_app.RestUtilities.Responses.FLoginResponse;
import com.geoxhonapps.physio_app.RestUtilities.RestController;
import com.geoxhonapps.physio_app.activities.HomeActivity;
import com.geoxhonapps.physio_app.activities.MainActivity;

import org.json.JSONException;

import java.io.IOException;

public class StaticFunctionUtilities {
    private static RestController restController = new RestController();
    private static AUser User;
    public static void attemptLoginToken(String refreshToken) {
        Thread athread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FLoginResponse r = restController.doLoginToken(refreshToken);
                    if (r.isSuccess) {
                        switch (r.accountType) {
                            case 0:
                                User = new AManagerUser(r);
                                break;
                            case 1:
                                User = new ADoctorUser(r);
                                break;
                            case 2:
                                User = new APatientUser(r);
                                break;
                        }
                    } else {
                        ContextFlowUtilities.dismissLoadingAlert();
                        ContextFlowUtilities.presentAlert("Σφάλμα", "Η σύνδεση δεν ήταν επιτυχής, παρακαλώ προσπαθήστε ξανά");
                    }
                } catch (IOException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Δεν υπάρχει σύνδεση στο διαδίκτυο");
                } catch (JSONException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Η σύνδεση δεν ήταν επιτυχής, παρακαλώ προσπαθήστε ξανά");
                }
            }
        });
        athread.start();
        try {
            athread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * Πραγματοποιεί την σύνδεση του χρήστη, έφοσον είναι επιτυχής μεταφερόμαστε στο HomeActivity
     * @param username Το όνομα χρήστη
     * @param password Ο κωδικός του χρήστη
     */
    public static void attemptLogin(String username, String password){
        ContextFlowUtilities.presentLoadingAlert("Παρακαλώ Περιμένετε", false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FLoginResponse r = restController.doLogin(username, password);
                    if (r.isSuccess) {
                        switch (r.accountType) {
                            case 0:
                                User = new AManagerUser(r);
                                break;
                            case 1:
                                User = new ADoctorUser(r);
                                break;
                            case 2:
                                User = new APatientUser(r);
                                break;
                        }

                    } else {
                        ContextFlowUtilities.dismissLoadingAlert();
                        ContextFlowUtilities.presentAlert("Σφάλμα", "Η σύνδεση δεν ήταν επιτυχής, παρακαλώ προσπαθήστε ξανά");
                    }
                } catch (IOException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Δεν υπάρχει σύνδεση στο διαδίκτυο");
                } catch (JSONException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Η σύνδεση δεν ήταν επιτυχής, παρακαλώ προσπαθήστε ξανά");
                }
            }
        }).start();
    }
    public static void attemptLogout(){
        ContextFlowUtilities.presentLoadingAlert("Παρακαλώ Περιμένετε", false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    restController.doLogout();
                    SharedPreferences sharedPreferences = ContextFlowUtilities.getCurrentView().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("refresh_token");
                    editor.apply();
                    User = null;
                    ContextFlowUtilities.moveTo(MainActivity.class, false);
                    ContextFlowUtilities.dismissLoadingAlert();
                } catch (IOException e) {
                    ContextFlowUtilities.dismissLoadingAlert();
                    ContextFlowUtilities.presentAlert("Σφάλμα", "Δεν υπάρχει σύνδεση στο διαδίκτυο");
                }
            }
        }).start();
    }

    /**
     * Επιστρέφει τον χρήστη για τον οποίο έχει γίνει σύνδεση
     * @return χρήστη για τον οποίο έχει γίνει σύνδεση
     */
    public static AUser getUser() {
        return User;
    }

    public static RestController getRestController(){return restController;}
}
