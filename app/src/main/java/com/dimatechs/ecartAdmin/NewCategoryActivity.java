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

import com.dimatechs.ecartAdmin.Model.Category;
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

public class NewCategoryActivity extends AppCompatActivity {

    private Button CreateCategoryBtn;
    private EditText EtCatName;
    private String saveCurrentDate,saveCurrentTime;
    private ProgressDialog loadingBar;
    private String catID="",catName;
    private DatabaseReference CatRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        catID=getIntent().getStringExtra("catID");
        CatRef= FirebaseDatabase.getInstance().getReference().child("Category");

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

    @Override
    protected void onStart() {
        super.onStart();
        if(catID!=null) {
            getCatDetails(catID);
        }
    }

    private void getCatDetails(String catID) {


        CatRef.child(catID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Category category=dataSnapshot.getValue(Category.class);
                    EtCatName.setText(category.getCatName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void CreateCategory() {

        catName = EtCatName.getText().toString();

        if (TextUtils.isEmpty(catName)) {
            Toast.makeText(this, "רשום קטגוריה בבקשה . . .", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("הוספת קטגוריה");
            loadingBar.setMessage("המתן בבקשה");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            if(catID==null)
            {
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate=currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime=currentTime.format(calendar.getTime());
                catID=saveCurrentDate+saveCurrentTime;
            }

            Validate();
        }
    }

        private void Validate()
        {
            HashMap<String,Object> catMap =  new HashMap<>();
            catMap.put("catID",catID);
            catMap.put("catName",catName);

         CatRef.child(catID).updateChildren(catMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Intent intent=new Intent(NewCategoryActivity.this,HomeActivity.class);
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(NewCategoryActivity.this, "נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                loadingBar.dismiss();
                                String message=task.getException().toString();
                                Toast.makeText(NewCategoryActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

