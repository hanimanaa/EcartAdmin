package com.dimatechs.ecartAdmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimatechs.ecartAdmin.Model.Category;
import com.dimatechs.ecartAdmin.ViewHolder.CategorysViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategorysActivity extends AppCompatActivity {

    private RecyclerView categorysList;
    private DatabaseReference categorystRef;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorys);

        categorysList=findViewById(R.id.categorys_list);
        categorysList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        categorysList.setLayoutManager(layoutManager);

        categorystRef =FirebaseDatabase.getInstance().getReference().child("Category");

    }
    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<Category> options=
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(categorystRef,Category.class)
                        .build();

        FirebaseRecyclerAdapter<Category, CategorysViewHolder> adapter=
                new FirebaseRecyclerAdapter<Category, CategorysViewHolder>(options) {


                    @NonNull
                    @Override
                    protected void onBindViewHolder(@NonNull CategorysViewHolder holder, int position, @NonNull final Category model) {
                        holder.txtcatName.setText(model.getCatName());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "עדכון קטגוריה",
                                                "הסרת קטגוריה"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CategorysActivity.this);
                                builder.setTitle("אפשריות : ");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(CategorysActivity.this, NewCategoryActivity.class);
                                            intent.putExtra("catID", model.getCatID());
                                            startActivity(intent);
                                        }
                                        if (i == 1) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(CategorysActivity.this);
                                            builder.setTitle("אזהרה");
                                            builder.setIcon(R.drawable.ic_red_forever_black_24dp);
                                            builder.setMessage("אתה עומד למחוק קטגוריה !!!");
                                            builder.setCancelable(true);
                                            builder.setPositiveButton("אני מסכים",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            categorystRef.child(model.getCatID())
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(CategorysActivity.this, "קטגוריה הוסרה בהצלחה", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });

                                                        }
                                                    }
                                            );
                                            builder.setNegativeButton("ביטול",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                        }
                                                    }

                                            );
                                            AlertDialog dialog=builder.create();
                                            dialog.show();

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public CategorysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items_layout,parent,false);
                        CategorysViewHolder holder = new CategorysViewHolder(view);
                        return holder;
                    }
                };

        categorysList.setAdapter(adapter);
        adapter.startListening();

    }

}
