package com.geoxhonapps.physio_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.geoxhonapps.physio_app.activities.ParentActivity;
public class ContextFlowUtilities {
    private static AppCompatActivity currentView = null;
    private static Object passedObject = null;
    private static ProgressDialog progressDialog = null;
    /**
     * Αλλάζει το View/Activity της εφαρμογής.
     * @param newView Η νέα κλάση που θα προβληθεί
     * @param bShouldGoBack Αν μετά την αλλαγή ο χρήστης θα μπορεί να γυρίσει στην προηγούμενη οθονη
     * @return Το instance της νέας οθόνης.
     */
    public static Intent moveTo(Class<? extends ParentActivity> newView, boolean bShouldGoBack){
        passedObject = null;
        Handler handler = new Handler(Looper.getMainLooper());
        Intent intent = new Intent(getCurrentView(), newView);
        handler.post(new Runnable() {
             @Override
             public void run() {
                 if(!bShouldGoBack){
                     // Set flags to create a new task and clear the existing task
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 }
                 // Start the new activity
                 getCurrentView().startActivity(intent);
             }
        });
        return intent;

    }
    /**
     * Αλλάζει το View/Activity της εφαρμογής, επιτρέπει επίσης να μεταφέρεις και ένα αντικείμενο στην νέα οθόνη
     * @param newView Η νέα κλάση που θα προβληθεί
     * @param bShouldGoBack Αν μετά την αλλαγή ο χρήστης θα μπορεί να γυρίσει στην προηγούμενη οθονη
     * @param objectToPass Το αντικείμενο που θα περάσει στην νέα οθόνη.
     * @return Το instance της νέας οθόνης.
     */
    public static Intent moveTo(Class<? extends ParentActivity> newView, boolean bShouldGoBack, Object objectToPass){
        passedObject = objectToPass;
        Handler handler = new Handler(Looper.getMainLooper());
        Intent intent = new Intent(getCurrentView(), newView);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!bShouldGoBack){
                    // Set flags to create a new task and clear the existing task
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                // Start the new activity
                getCurrentView().startActivity(intent);
            }
        });
        return intent;

    }
    /**
     * Επιστρέφει την οθόνη που προβάλλεται τώρα στην εφαρμογή
     * @return οθόνη που προβάλλεται τώρα στην εφαρμογή
     */
    public static AppCompatActivity getCurrentView() {
        return currentView;
    }

    public static Object getPassedObject(){return passedObject;}

    public static void setCurrentView(AppCompatActivity currentView) {
        ContextFlowUtilities.currentView = currentView;
    }
    /**
     * Εμφανίζει ένα Alert/Ειδοποιήση σέ όλη την εφαρμογή
     * @param alertTitle Ο τίτλος
     * @param alertMessage Οι λεπτομέρειες
     */
    public static void presentAlert(String alertTitle, String alertMessage){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Create an AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(currentView);

                // Set the dialog title and message
                builder.setTitle(alertTitle);
                builder.setMessage(alertMessage);

                // Add an "OK" button to the dialog
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when the OK button is clicked
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public static void presentLoadingAlert(String alertMessage, boolean bIsCancellable){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(currentView);
                progressDialog.setMessage(alertMessage);
                progressDialog.setCancelable(bIsCancellable); // Set to true if you want to allow cancellation
                progressDialog.show();
            }
        });
    }
    public static void dismissLoadingAlert(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog!=null){
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
}
