package com.dimatechs.ecart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.ecart.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef,usersref;
    private String phone= "han";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList=findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef,AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {
                        holder.userName.setText(model.getName());
                        holder.userPhoneNumber.setText(model.getPhone());
                        holder.userTotalPrice.setText(" מחיר : " +model.getTotalAmount() + " ש\"ח ");
                        holder.userDateTime.setText(model.getDate()+" "+model.getTime());
                        holder.userAdress.setText(model.getAddress());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "הסרת הזמנה"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("אפשריות : ");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0) {
                                            ordersRef.child(getRef(position).getKey()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                Toast.makeText(AdminNewOrdersActivity.this, "הזמנה נמחקה בהצלחה", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });




                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String orderNum = getRef(position).getKey();
                                String x=getRef(position).child(phone).getKey();
                                Toast.makeText(AdminNewOrdersActivity.this, x, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                                intent.putExtra("orderNum",orderNum);
                                startActivity(intent);
                            }
                        });



                    }
                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
                    {
                        Log.d("aaaa","onCreateViewHolder");

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        AdminOrdersViewHolder holder = new AdminOrdersViewHolder(view);
                        return holder;
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();


    }

    public void getPhoneF(String orderNum)
    {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Orders").child(orderNum);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                AdminOrders order = dataSnapshot.getValue(AdminOrders.class);
                phone=order.getPhone();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(AdminNewOrdersActivity.this, "onClick after "+phone, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
        intent.putExtra("orderNum",orderNum);
        startActivity(intent);
    }







    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userAdress;
        public Button showOrdersBtn;

        public AdminOrdersViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = (TextView) itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = (TextView) itemView.findViewById(R.id.order_total_price);
            userDateTime = (TextView) itemView.findViewById(R.id.order_date_time);
            userAdress = (TextView) itemView.findViewById(R.id.order_address_city);
            showOrdersBtn = (Button) itemView.findViewById(R.id.show_all_products_btn);




        }
    }
}
