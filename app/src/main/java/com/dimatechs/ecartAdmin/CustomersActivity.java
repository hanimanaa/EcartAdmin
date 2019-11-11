package com.dimatechs.ecartAdmin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dimatechs.ecartAdmin.Model.AdminOrders;
import com.dimatechs.ecartAdmin.Model.Cart;
import com.dimatechs.ecartAdmin.Model.Users;
import com.dimatechs.ecartAdmin.ViewHolder.CartViewHolder;
import com.dimatechs.ecartAdmin.ViewHolder.CustomersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomersActivity extends AppCompatActivity {

    private RecyclerView customersList;
    private DatabaseReference customersListRef;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        customersList=findViewById(R.id.customers_list);
        customersList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        customersList.setLayoutManager(layoutManager);

        customersListRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");
    }
    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Users customer = dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Users> options=
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(customersListRef,Users.class)
                        .build();


        FirebaseRecyclerAdapter<Users, CustomersViewHolder> adapter=
                new FirebaseRecyclerAdapter<Users, CustomersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CustomersViewHolder holder, int position, @NonNull final Users model)
                    {
                        holder.txtcustomer_bisiness.setText("שם עסק : "+ model.getBisiness());
                        holder.txtcustomer_city.setText("עיר : "+ model.getCity());
                        holder.txtcustomer_name.setText("איש קשר : "+ model.getFname() + " "+model.getLname());
                        holder.txtcustomer_phone.setText("טלפון : "+ model.getPhone());
                    }
                    @NonNull
                    @Override
                    public CustomersViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_items_layout,parent,false);
                        CustomersViewHolder holder = new CustomersViewHolder(view);
                        return holder;
                    }
                };
        customersList.setAdapter(adapter);
        adapter.startListening();

    }


}

