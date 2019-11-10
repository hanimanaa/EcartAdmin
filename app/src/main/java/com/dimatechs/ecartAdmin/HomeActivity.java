package com.dimatechs.ecartAdmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dimatechs.ecartAdmin.Model.Products;
import com.dimatechs.ecartAdmin.Prevalent.Prevalent;
import com.dimatechs.ecartAdmin.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button RegisterAccountBtn;
    private FloatingActionButton btn;
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        btn = (FloatingActionButton) findViewById(R.id.btn);
        btn.setOnClickListener(this);

        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView=findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void onClick(View view) {
        if(view==RegisterAccountBtn) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
        else if (view==btn){
            Intent intent = new Intent(getApplicationContext(), AdminNewOrdersActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef,Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText(" מחיר : " +model.getPrice() + " ש\"ח ");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "עדכון מוצר",
                                                "הסרת מוצר"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                builder.setTitle("אפשריות : ");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(HomeActivity.this, NewProductActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (i == 1) {
                                            ProductsRef.child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(HomeActivity.this, "המוצר הוסר בהצלחה", Toast.LENGTH_SHORT).show();
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

                    @Override
                    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Register) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.action_Admin) {
            Intent intent = new Intent(getApplicationContext(),NewProductActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.action_Orders) {
            Intent intent = new Intent(getApplicationContext(),AdminNewOrdersActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.action_Customers) {
            Intent intent = new Intent(getApplicationContext(),CustomersActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.action_Exit) {
            Toast.makeText(this,"יציאה",Toast.LENGTH_LONG).show();
            //   android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(1);
            // Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return true;
    }
}