package com.howtechworx.referralappdomilearn;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.howtechworx.referralappdomilearn.model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText name,email,referral;
    private Button submit;
    private String userEmail="",userName="",referralCode="";
    private FirebaseFirestore registrationDB,referredUsersDB,emailsDB;
    private HashMap<String,Object> newUser = new HashMap<>();
    private TextView amount;
    private static List<String> userKeys = new ArrayList<>();
    private Integer INITIAL_AMOUNT =100;
    private Integer BONUS =100;
    private static String CURRENT_USER_KEY,CURRENT_USER_EMAIL;
    private Integer S_NO=0;
    private static List<UserData> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        getUserData();
        setListeners();
    }

    private void getUserData() {
        registrationDB.collection("Registration")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("", document.getId() + " => " + document.getData());
                              //  userKeys.add(document.getId());
                                UserData userData= document.toObject(UserData.class).withId(document.getId());
                                userKeys.add(userData.getId());
                                usersList.add(userData);
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

        private void initUI() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
        referral = findViewById(R.id.referral);
        amount = findViewById(R.id.amount);

        registrationDB = FirebaseFirestore.getInstance();
        referredUsersDB = FirebaseFirestore.getInstance();
       // emailsDB = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertToString(name,email,referral);
                addUser();
            }
        });
    }

    private void convertToString(EditText name, EditText email, EditText referral) {
        userName = name.getText().toString();
        userEmail = email.getText().toString();
        referralCode = referral.getText().toString();
    }

    private void addUser() {
        if(userName.isEmpty()&& userEmail.isEmpty())
            Toast.makeText(MainActivity.this, "Enter the above details", Toast.LENGTH_SHORT).show();

        else if(userEmail.isEmpty() || userName.isEmpty())
            Toast.makeText(MainActivity.this, "You missed a field", Toast.LENGTH_SHORT).show();

   /*     else if(emailIDs.contains(userEmail))
            Toast.makeText(this, "This email id is already registered", Toast.LENGTH_SHORT).show();
*/
        else
        {
         //   emailIDs.add(userEmail);
            newUser.put("name",userName);
            newUser.put("email",userEmail);
            newUser.put("referral",referralCode);
            newUser.put("amount",INITIAL_AMOUNT);
            registrationDB.collection("Registration")
                    .add(newUser)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "User Registered", Toast.LENGTH_SHORT).show();

                            CURRENT_USER_KEY = documentReference.getId();
                            Toast.makeText(MainActivity.this, CURRENT_USER_KEY+"1", Toast.LENGTH_SHORT).show();

                            searchReferral(referralCode,userKeys,CURRENT_USER_KEY);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void searchReferral(String referralCode, List<String> userKeys, String CURRENT_USER_KEY) {
        boolean found = false;

        Iterator iterator = userKeys.iterator();
        String currentCode="";

        while(iterator.hasNext())
        {
            currentCode= (String) iterator.next();
            if(currentCode.equals(referralCode)) {
                found = true;
                break;
            }
        }

        if(found)
        {

            HashMap<String,String> id = new HashMap<>();
            id.put("referred by :",referralCode);
            referredUsersDB.collection("Referred Users")
                    .add(id);

         //   UpdateDataReferredBy(referralCode);
            UpdateDataReferredTo(CURRENT_USER_KEY);
            Toast.makeText(this, "ref", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(this, "no refer", Toast.LENGTH_SHORT).show();
        //    int finalAmount = INITIAL_AMOUNT;
        //    amount.setText(finalAmount + "");
            amount.setVisibility(View.VISIBLE);
        }
    }

    private void UpdateDataReferredTo(String CURRENT_USER_KEY) {
        Toast.makeText(this, CURRENT_USER_KEY +"2", Toast.LENGTH_SHORT).show();

        for( final UserData d : usersList){
            Toast.makeText(this, CURRENT_USER_KEY +"3", Toast.LENGTH_SHORT).show();
            if(d.getId().contains(CURRENT_USER_KEY))
            {
                Toast.makeText(this, CURRENT_USER_KEY +"4", Toast.LENGTH_SHORT).show();
                int finalAmount = d.getAmount() + BONUS;
                DocumentReference documentReference = referredUsersDB.collection("Registration").document(CURRENT_USER_KEY);
                documentReference.update("amount",finalAmount +"")
                        .addOnSuccessListener(new OnSuccessListener < Void > () {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Accounts Updated Successfully",
                                        Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this,
                                        "Referral code matched ,you get a bonus", Toast.LENGTH_SHORT).show();
                                amount.setText(d.getAmount()+" ");
                                amount.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
    }

    private void UpdateDataReferredBy(String referralCode) {

        for( UserData d : usersList){
            if(d.getId() != null && d.getId().contains(referralCode))
            {
                int finalAmount = d.getAmount() + BONUS;
                DocumentReference registration = registrationDB.collection("Registration").document(referralCode);
                registration.update("amount", finalAmount+"")
                        .addOnSuccessListener(new OnSuccessListener < Void > () {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Accounts Updated Successfully",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

}}
