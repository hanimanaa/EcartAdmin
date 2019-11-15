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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    private Button CreateCategoryBtn;
    private EditText EtCatName;
    private String saveCurrentDate,saveCurrentTime;
    private ProgressDialog loadingBar;
    private String catID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        CreateCategoryBtn=(Button)findViewById(R.id.AddCat_btn);
        EtCatName=(EditText)findViewById(R.id.EtCatName);

        loadingBar=new ProgressDialog(this);

        CreateCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCategory();
            }
        });
    }

    private void CreateCategory() {
        String catName = EtCatName.getText().toString();

        if (TextUtils.isEmpty(catName)) {
            Toast.makeText(this, "רשום קטיגוריה בבקשה . . .", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("הוספת קטיגוריה");
            loadingBar.setMessage("המתן בבקשה");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Calendar calendar=Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate=currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime=currentTime.format(calendar.getTime());

            catID=saveCurrentDate+saveCurrentTime;

            Validate(catName);
        }
    }

        private void Validate(final String catName)
        {
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(!dataSnapshot.child("Category").child(catID).exists())
                    {
                        HashMap<String,Object> dataMap = new HashMap<>();
                        dataMap.put("catID",catID);
                        dataMap.put("catName",catName);
                        RootRef.child("Category").child(catID).updateChildren(dataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(CategoryActivity.this, "נרשם בהצלחה", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                            Intent intent=new Intent(CategoryActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(CategoryActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(CategoryActivity.this, "קטיגוריה קיימת !!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Intent intent=new Intent(CategoryActivity.this, HomeActivity.class);
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

