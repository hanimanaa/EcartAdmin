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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.ecart.Model.Cart;
import com.dimatechs.ecart.Prevalent.Prevalent;
import com.dimatechs.ecart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity
{
    private  RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextprocessBtn;
    private TextView txtTotalAmount,txtMsg1;
    private ImageView image;

    private int overTotalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Paper.init(this);


        recyclerView=(RecyclerView)findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextprocessBtn=(Button)findViewById(R.id.next_process_btn);
        txtTotalAmount=(TextView)findViewById(R.id.total_price);
        txtMsg1=(TextView)findViewById(R.id.msg1);
        image=(ImageView)findViewById(R.id.shop_cart);


        NextprocessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                txtTotalAmount.setText("Total Price" + String.valueOf(overTotalPrice));
                String st=" סה\"כ : " + String.valueOf(overTotalPrice) + " ש\"ח ";
                Toast.makeText(CartActivity.this, st, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();

            }
        });

    }

    private void check()
    {
        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()) ;

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.exists() || Paper.book().read(Prevalent.UserOrderKey) == null)
                {
                    recyclerView.setVisibility(View.GONE);
                    txtMsg1.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    NextprocessBtn.setVisibility(View.GONE);
                }
                else
                    loadProducts();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadProducts()
    {
        final DatabaseReference CartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        Log.d("cart", "onStart");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(CartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone())
                                .child(Paper.book().read(Prevalent.UserOrderKey).toString())
                                .child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                        Log.d("cart", "onBindViewHolder");
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText(" מחיר : " + model.getPrice() + " ש\"ח ");
                        holder.txtProductQuantity.setText(" כמות : " + model.getQuantity());

                        int oneTypeProductTPrice = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                        overTotalPrice = overTotalPrice + oneTypeProductTPrice;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "עדכון כמות",
                                                "הסרת מוצר"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("אפשריות : ");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (i == 1) {
                                            CartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child(Paper.book().read(Prevalent.UserOrderKey).toString())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                CartListRef.child("Admin View")
                                                                        .child(Prevalent.currentOnlineUser.getPhone())
                                                                        .child(Paper.book().read(Prevalent.UserOrderKey).toString())
                                                                        .child("Products")
                                                                        .child(model.getPid())
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(CartActivity.this, "המוצר הוסר בהצלחה", Toast.LENGTH_SHORT).show();
                                                                                    //   Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                                                    //   startActivity(intent);
                                                                                }

                                                                            }
                                                                        });
                                                            }

                                                        }
                                                    });

                                        }
                                    }
                                });
                                builder.show();

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.d("cart", "onCreateViewHolder");

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        check();


    }

}
