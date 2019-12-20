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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.dimatechs.ecartAdmin.Model.Products;
import com.dimatechs.ecartAdmin.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button RegisterAccountBtn;
    private FloatingActionButton btn;
    private DatabaseReference ProductsRef,CategoryRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private Spinner spinner2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        btn = (FloatingActionButton) findViewById(R.id.btn);
        btn.setOnClickListener(this);

        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");
        CategoryRef=FirebaseDatabase.getInstance().getReference().child("Category");

        spinner2=(Spinner) findViewById(R.id.spinner2);
        recyclerView=findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //fill spinner
        Query query1 = CategoryRef.orderByChild("catName");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> catList = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String catName =dataSnapshot1.child("catName").getValue(String.class);
                    catList.add(catName);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HomeActivity.this,android.R.layout.simple_spinner_item,catList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        //filter recyclerView by spinner
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCategory = spinner2.getSelectedItem().toString();
                if(selectedCategory.equals("הצג הכל"))
                {
                    Query query = ProductsRef;
                    fillRecyclerView(query);
                }
                else
                {
                    Query query = ProductsRef.orderByChild("category").equalTo(selectedCategory);
                    fillRecyclerView(query);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fillRecyclerView(ProductsRef);
            }
        });


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
    }

    private void fillRecyclerView(Query query) {

        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query,Products.class)
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

        else if (id == R.id.action_Category) {
            Intent intent = new Intent(getApplicationContext(),CategorysActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_Add_Category) {
            Intent intent = new Intent(getApplicationContext(), NewCategoryActivity.class);
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