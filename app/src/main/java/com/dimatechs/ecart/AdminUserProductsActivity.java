package com.dimatechs.ecart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimatechs.ecart.Model.AdminOrders;
import com.dimatechs.ecart.Model.Cart;
import com.dimatechs.ecart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    private DatabaseReference cartListRef;
    RecyclerView.LayoutManager layoutManager;
    private String orderNum ="";
    public String phonex ="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        orderNum =getIntent().getStringExtra("orderNum");
       // phone =getIntent().getStringExtra("phone");

        productsList=findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);




        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(orderNum)
                .child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Orders").child(orderNum);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                AdminOrders order = dataSnapshot.getValue(AdminOrders.class);
                phonex=order.getPhone();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, phonex, Toast.LENGTH_SHORT).show();

        FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef,Cart.class)
                        .build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
                    {
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText( " מחיר : " + model.getPrice()+ " ש\"ח ");
                        holder.txtProductQuantity.setText(" כמות : " + model.getQuantity());
                    }
                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        productsList.setAdapter(adapter);
        adapter.startListening();

    }


}
