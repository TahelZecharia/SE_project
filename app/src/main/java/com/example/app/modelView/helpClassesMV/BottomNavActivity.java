package com.example.app.modelView.helpClassesMV;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.app.R;
import com.example.app.View.MainActivity;
import com.example.app.View.UsersActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private BottomNavigationView nav;

    /**
     * Navigate on the menu bar of admin
     * @param activity the curr activity
     */
    public void bNav(Activity activity) {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(activity,gso);
        nav = activity.findViewById(R.id.bottom_nav_admi);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sign_out_admin://Sign out
                        areYouSureMessage(gsc,activity);
                        break;

                    case R.id.u: //nav to users list
                        activity.startActivity(new Intent(activity, UsersActivity.class));
                        break;

                    case R.id.p: //nav to sitess list
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        break;

                }
                return true;
            }
        });
    }

    private void areYouSureMessage(GoogleSignInClient gsc, Activity activity){
        new AlertDialog.Builder(activity).setMessage("Are you sure you want to exit?").
                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ShowToastAndSignOut showToastAndSignOut = new ShowToastAndSignOut();
                        showToastAndSignOut.signOut(gsc,activity);
//                        signOut();
                    }
                })
                .setNegativeButton("No",null).show();
    }
}
