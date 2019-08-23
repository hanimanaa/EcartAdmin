package com.dimatechs.ecartAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountBtn;
    private EditText Etbusiness,Etfname,Etlname,Etphone,Etcity,Etpassword;
    private ProgressDialog loadingBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountBtn=(Button)findViewById(R.id.Add_btn);
        Etbusiness=(EditText)findViewById(R.id.Etbusiness);
        Etfname=(EditText)findViewById(R.id.Etfname);
        Etlname=(EditText)findViewById(R.id.Etlname);
        Etphone=(EditText)findViewById(R.id.Etphone);
        Etcity=(EditText)findViewById(R.id.Etcity);
        Etpassword=(EditText)findViewById(R.id.Etpassword);
        loadingBar=new ProgressDialog(this);
        
        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount()
    {
        String bisiness =Etbusiness.getText().toString();
        String fname =Etfname.getText().toString();
        String lname =Etlname.getText().toString();
        String phone =Etphone.getText().toString();
        String city =Etcity.getText().toString();
        String password =Etpassword.getText().toString();


        if(TextUtils.isEmpty(bisiness))
        {
            Toast.makeText(this, "רשום שם עסק בבקשה . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fname))
        {
            Toast.makeText(this, "רשום שם פרטי בבקשה . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lname))
        {
            Toast.makeText(this, "רשום שם משפחה בבקשה . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "רשום מס טלפון בבקשה . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city))
        {
            Toast.makeText(this, "רשום עיר בבקשה . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "רשום סיסמה בבקשה . . .", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("יצירת חשבון");
            loadingBar.setMessage("המתן בבקשה");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            
            ValidatePhoneNumber(phone,bisiness,fname,lname,city,password);
        }
    }

    private void ValidatePhoneNumber(final String phone,final String bisiness,final String fname,final String lname,final String city,final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.child("Users").child(phone).exists())
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("bisiness",bisiness);
                    userdataMap.put("fname",fname);
                    userdataMap.put("lname",lname);
                    userdataMap.put("phone",phone);
                    userdataMap.put("city",city);
                    userdataMap.put("password",password);


                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "לקוח נרשם בהצלחה", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "מס טלפון רשום !!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
