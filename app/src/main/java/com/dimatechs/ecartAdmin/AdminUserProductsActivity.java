package com.dimatechs.ecartAdmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimatechs.ecartAdmin.Model.AdminOrders;
import com.dimatechs.ecartAdmin.Model.Cart;
import com.dimatechs.ecartAdmin.ViewHolder.PdfCreateAdapter;
import com.dimatechs.ecartAdmin.utils.PDFCreationUtils;
import com.dimatechs.ecartAdmin.utils.PdfBitmapCache;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    private DatabaseReference cartListRef;
    RecyclerView.LayoutManager layoutManager;
    private String orderNum = "";
    public String phonex = "";


    private boolean IS_MANY_PDF_FILE;

    /**
     * This is identify to number of pdf file. If pdf model list size > sector so we have create many file. After that we have merge all pdf file into one pdf file
     */
    private int SECTOR = 100; // Default value for one pdf file.
    private int START;
    private int END = SECTOR;
    private int NO_OF_PDF_FILE = 1;
    private int NO_OF_FILE;
    private int LIST_SIZE;
    private ProgressDialog progressDialog;


    /**
     * Store all  PDF models
     */
    private List<Cart> pdfModels = new ArrayList<>();
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        orderNum =getIntent().getStringExtra("orderNum");
        // phone =getIntent().getStringExtra("phone");

        productsList = findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);


        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(orderNum)
                .child("Products");


        findViewById(R.id.btn_print_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            generatePdfReport();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        } else {
            generatePdfReport();
        }
    }

    /**
     * This is manage to all model
     */
    private void generatePdfReport() {


        // NO_OF_FILE : This is identify to one file or many file have to created

        LIST_SIZE = pdfModels.size();

        NO_OF_FILE = LIST_SIZE / SECTOR;
        if (LIST_SIZE % SECTOR != 0) {
            NO_OF_FILE++;
        }
        if (LIST_SIZE > SECTOR) {
            IS_MANY_PDF_FILE = true;
        } else {
            END = LIST_SIZE;
        }
        createPDFFile();
    }

    private void createProgressBarForPDFCreation(int maxProgress) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(String.format(getString(R.string.msg_progress_pdf), String.valueOf(maxProgress)));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgress);
        progressDialog.show();
    }

    private void createProgressBarForMergePDF() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.msg_progress_merger_pdf));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * This function call with recursion
     * This recursion depend on number of file (NO_OF_PDF_FILE)
     */
    private void createPDFFile() {

        // Find sub list for per pdf file data
        List<Cart> pdfDataList = pdfModels.subList(START, END);
        PdfBitmapCache.clearMemory();
        PdfBitmapCache.initBitmapCache(getApplicationContext());
        final PDFCreationUtils pdfCreationUtils = new PDFCreationUtils(AdminUserProductsActivity.this, pdfDataList, LIST_SIZE, NO_OF_PDF_FILE);
        if (NO_OF_PDF_FILE == 1) {
            createProgressBarForPDFCreation(PDFCreationUtils.TOTAL_PROGRESS_BAR);
        }
        pdfCreationUtils.createPDF(new PDFCreationUtils.PDFCallback() {

            @Override
            public void onProgress(final int i) {
                progressDialog.setProgress(i);
            }

            @Override
            public void onCreateEveryPdfFile() {
                // Execute may pdf files and this is depend on NO_OF_FILE
                if (IS_MANY_PDF_FILE) {
                    NO_OF_PDF_FILE++;
                    if (NO_OF_FILE == NO_OF_PDF_FILE - 1) {

                        progressDialog.dismiss();
                        createProgressBarForMergePDF();
                        pdfCreationUtils.downloadAndCombinePDFs();
                    } else {

                        // This is identify to manage sub list of current pdf model list data with START and END

                        START = END;
                        if (LIST_SIZE % SECTOR != 0) {
                            if (NO_OF_FILE == NO_OF_PDF_FILE) {
                                END = (START - SECTOR) + LIST_SIZE % SECTOR;
                            }
                        }
                        END = SECTOR + END;
                        createPDFFile();
                    }

                } else {
                    // Merge one pdf file when all file is downloaded
                    progressDialog.dismiss();

                    createProgressBarForMergePDF();
                    pdfCreationUtils.downloadAndCombinePDFs();
                }

            }

            @Override
            public void onComplete(final String filePath) {
                progressDialog.dismiss();
                if (filePath != null) {
                    OpenPDFFile(filePath);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminUserProductsActivity.this, "Error  " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void OpenPDFFile(String filePath) {

        File file = new File(filePath);
        Intent target = new Intent(Intent.ACTION_VIEW);
       // target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
/*
        Intent intent = Intent.createChooser(target, "המתן בבקשה");
        try
        {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(this, "תקלה", Toast.LENGTH_SHORT).show();
        }
*/

        Uri apkURI = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName(), file);
        target.setDataAndType(apkURI, "application/pdf");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(target);
    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Orders").child(orderNum);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminOrders order = dataSnapshot.getValue(AdminOrders.class);
                phonex=order.getPhone();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();


        FirebaseRecyclerAdapter<Cart, PdfCreateAdapter.CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, PdfCreateAdapter.CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PdfCreateAdapter.CartViewHolder holder, int position, @NonNull final Cart model) {

                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText(" מחיר : " + model.getPrice() + " ש\"ח ");
                        holder.txtProductQuantity.setText(" כמות : " + model.getQuantity());

                        Cart cart = new Cart();
                        cart.setName(model.getName());
                        cart.setPrice(model.getPrice());
                        cart.setQuantity(model.getQuantity());
                        pdfModels.add(cart);
                    }

                    @NonNull
                    @Override
                    public PdfCreateAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        PdfCreateAdapter.CartViewHolder holder = new PdfCreateAdapter.CartViewHolder(view);
                        return holder;
                    }
                };
        productsList.setAdapter(adapter);
        adapter.startListening();

    }


}