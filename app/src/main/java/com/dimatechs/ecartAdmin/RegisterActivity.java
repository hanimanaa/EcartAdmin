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

import com.dimatechs.ecartAdmin.Model.Users;
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
    private String CustomerPhone="";
    private String business,fname,lname,phone,city,password;
    private DatabaseReference CustomerRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CustomerPhone=getIntent().getStringExtra("phone");


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

    @Override
    protected void onStart() {
        super.onStart();
        if(CustomerPhone!=null) {
            getCustomerDetails(CustomerPhone);
        }
    }

    private void getCustomerDetails(String customerPhone) {
        CustomerRef= FirebaseDatabase.getInstance().getReference().child("Users");

        CustomerRef.child(customerPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    Etbusiness.setText(users.getBisiness());
                    Etfname.setText(users.getFname());
                    Etlname.setText(users.getLname());
                    Etphone.setText(users.getPhone());
                    Etcity.setText(users.getCity());
                    Etpassword.setText(users.getPassword());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void CreateAccount()
    {
        business =Etbusiness.getText().toString();
        fname =Etfname.getText().toString();
        lname =Etlname.getText().toString();
        phone =Etphone.getText().toString();
        city =Etcity.getText().toString();
        password =Etpassword.getText().toString();


        if(TextUtils.isEmpty(business))
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
            
            ValidatePhoneNumber(phone,business,fname,lname,city,password);
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
                if(CustomerPhone!=null || !dataSnapshot.child("Users").child(phone).exists())
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
                    Toast.makeText(RegisterActivity.this, "מס טלפון רשום !", Toast.LENGTH_SHORT).show();
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

    private void SaveCustomerInfoToDatabase()
    {
        HashMap<String,Object> productMap =  new HashMap<>();
        productMap.put("bisiness",business);
        productMap.put("city",city);
        productMap.put("fname",fname);
        productMap.put("lname",lname);
        productMap.put("phone",phone);
        productMap.put("password",password);


        CustomerRef.child(phone).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(RegisterActivity.this,CustomersActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "לקוח נרשם בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(RegisterActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

    }
}
