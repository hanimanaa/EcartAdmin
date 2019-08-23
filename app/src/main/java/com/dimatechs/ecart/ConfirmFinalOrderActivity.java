package com.dimatechs.ecart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dimatechs.ecart.Model.Users;
import com.dimatechs.ecart.Prevalent.Prevalent;
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

import io.paperdb.Paper;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private Button confirmOrderBtn;
    private String totalAmount="";
    String name="",address="";
    private DatabaseReference usersref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        Paper.init(this);

        totalAmount=getIntent().getStringExtra("Total Price");
        confirmOrderBtn=(Button)findViewById(R.id.confirm_final_order_btn);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ConfirmOrder();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(this, Prevalent.currentOnlineUser.getPhone(), Toast.LENGTH_SHORT).show();
        usersref = FirebaseDatabase.getInstance().getReference();
        usersref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).exists())
                 {
                    Users usersData =dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).getValue(Users.class);
                    name = usersData.getFname() +" "+usersData.getLname();
                    address=usersData.getCity();
                 }
                else
                    Toast.makeText(ConfirmFinalOrderActivity.this, "no name", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ConfirmOrder()
    {

       final String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Paper.book().read(Prevalent.UserOrderKey).toString());

        final HashMap<String,Object> ordersMap =  new HashMap<>();
        ordersMap.put("name",name);
        ordersMap.put("orderNum",Paper.book().read(Prevalent.UserOrderKey).toString());
        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state","not shipped");
        ordersMap.put("address",address);
        ordersMap.put("phone",Prevalent.currentOnlineUser.getPhone());

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "ההזמנה נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }

            }
        });




    }
}
