package com.dimatechs.ecart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.ecart.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

;

public class SettinsActivity extends AppCompatActivity {
    private EditText bisinessEditText, fnameEditText, lnameEditText, cityEditText, passswordEditText;
    private TextView closeTextBtn, saveTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settins);


        bisinessEditText = (EditText) findViewById(R.id.settings_bisiness);
        fnameEditText = (EditText) findViewById(R.id.settings_fname);
        lnameEditText = (EditText) findViewById(R.id.settings_lname);
        cityEditText = (EditText) findViewById(R.id.settings_city);
        passswordEditText = (EditText) findViewById(R.id.settings_password);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);


        userInfoDisplay(bisinessEditText, fnameEditText, lnameEditText, cityEditText, passswordEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOnlyUserInfo();
            }
        });

    }


    private void updateOnlyUserInfo() {
        if (TextUtils.isEmpty(bisinessEditText.getText().toString())) {
            Toast.makeText(this, "חובה להזין שם בית עסק", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fnameEditText.getText().toString())) {
            Toast.makeText(this, "חובה להזין שם פרטי", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lnameEditText.getText().toString())) {
            Toast.makeText(this, "חובה להזין שם משפחה", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passswordEditText.getText().toString())) {
            Toast.makeText(this, "חובה להזין סיסמה", Toast.LENGTH_SHORT).show();
        } else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("bisiness", bisinessEditText.getText().toString());
            userMap.put("fname", fnameEditText.getText().toString());
            userMap.put("lname", lnameEditText.getText().toString());
            userMap.put("city", cityEditText.getText().toString());
            userMap.put("password", passswordEditText.getText().toString());

            ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

            startActivity(new Intent(SettinsActivity.this, HomeActivity.class));
            Toast.makeText(SettinsActivity.this, "עודכן בהצלחה . .", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void userInfoDisplay(final EditText bisinessEditText, final EditText fnameEditText, final EditText lnameEditText, final EditText cityEditText, final EditText passswordEditText) {
        {
            DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

            UsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("phone").exists()) {

                            String bisiness = dataSnapshot.child("bisiness").getValue().toString();
                            String fname = dataSnapshot.child("fname").getValue().toString();
                            String lname = dataSnapshot.child("lname").getValue().toString();
                            String city = dataSnapshot.child("city").getValue().toString();
                            String password = dataSnapshot.child("password").getValue().toString();


                            bisinessEditText.setText(bisiness);
                            fnameEditText.setText(fname);
                            lnameEditText.setText(lname);
                            cityEditText.setText(city);
                            passswordEditText.setText(password);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
