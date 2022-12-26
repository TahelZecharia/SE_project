package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText user;
    private Button create;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        create = findViewById(R.id.create);
        password = findViewById(R.id.password);
        user = findViewById(R.id.Username);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String pas_email = password.getText().toString();
                if(TextUtils.isEmpty(txt_email )|| TextUtils.isEmpty(pas_email)){
                    Toast.makeText(LoginActivity.this, "empty!", Toast.LENGTH_LONG).show();
                }
                else if(pas_email.length()<6){
                    Toast.makeText(LoginActivity.this, "less than 6 chars", Toast.LENGTH_LONG).show();
                }
                else{
                    notBlocked(txt_email,pas_email);
                }
            }
        });
    }

    private void regisuser(String txt_email, String pas_email) {
        auth.createUserWithEmailAndPassword(txt_email, pas_email).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                        FirebaseUser fu = auth.getCurrentUser();
                        fu.sendEmailVerification();
                        DocumentReference df = fstore.collection("Users").document(fu.getUid());
                        Map<String, Object> us_info = new HashMap<>();
                        us_info.put("ID", fu.getUid());
                        us_info.put("Email", email.getText().toString());
                        us_info.put("isUser", "1");
                        us_info.put("LettersNum", "0");
                        FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(us_info);
                        df.set(us_info);
                        Toast.makeText(LoginActivity.this, "success, verify email", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, StartActivity.class));
                        finish();

                }
                else{
                    Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void notBlocked(String e_mail, String pass) {
        fstore.collection("BlockedUsers").whereEqualTo("Email",e_mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int size = querySnapshot.size();
                            if (size != 0) {
                                auth.signOut();
                                Toast.makeText(LoginActivity.this, "You are blocked", Toast.LENGTH_LONG).show();
                            } else {
                                regisuser(e_mail, pass);
                            }
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

