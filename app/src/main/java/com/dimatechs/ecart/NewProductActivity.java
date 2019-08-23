package com.dimatechs.ecart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimatechs.ecart.Model.Products;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class NewProductActivity extends AppCompatActivity {

    private Button AddNewProductBtn;
    private EditText EDProductName,EDProuctDescription,EdProductPrice;
    private ImageView ProductImage;
    private static final int GalleryPick=1;
    private Uri ImageUri,oldImageUri;
    private String ProductName ,ProuctDescription,ProductPrice,saveCurrentDate,saveCurrentTime;
    private String productRandomKey,downloadImageURL;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private String productID="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        productID=getIntent().getStringExtra("pid");


        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef=FirebaseDatabase.getInstance().getReference().child("Products");

        loadingBar=new ProgressDialog(this);
        AddNewProductBtn=(Button)findViewById(R.id.Btn_new_product);
        EDProductName=(EditText) findViewById(R.id.Ed_Product_name);
        EDProuctDescription=(EditText) findViewById(R.id.Ed_Product_Description);
        EdProductPrice=(EditText) findViewById(R.id.Ed_Product_Price);
        ProductImage=(ImageView) findViewById(R.id.product_image);

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        AddNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }

    @Override
    protected void onStart() {
            super.onStart();
            if(productID!=null) {
                getProductDetails(productID);
            }

    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);

                    EDProductName.setText(products.getName());
                    EdProductPrice.setText(products.getPrice());
                    EDProuctDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(ProductImage);
                    oldImageUri= Uri.parse(products.getImage());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GalleryPick && resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri=data.getData();
            ProductImage.setImageURI(ImageUri);
        }

    }

    private void ValidateProductData() {
        ProductName=EDProductName.getText().toString();
        ProuctDescription=EDProuctDescription.getText().toString();
        ProductPrice=EdProductPrice.getText().toString();

        if(oldImageUri ==null && ImageUri==null)
        {
            Toast.makeText(this, "חסר תמונה למוצר", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ProductName))
        {
            Toast.makeText(this, "חסר שם מוצר . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ProuctDescription))
        {
            Toast.makeText(this, "חסר תאור למוצר . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(ProductPrice))
        {
            Toast.makeText(this, "חסר מחיר למוצר . . .", Toast.LENGTH_SHORT).show();
        }
        else 
        {
            StoreProductInformation();
        }


    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("הוספת מוצר חדש");
        loadingBar.setMessage("המתן בבקשה");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        if(productID !=null)
        { //update
            productRandomKey=productID;

            if(ImageUri !=null && ImageUri.toString() != oldImageUri.toString() )
            {
                Toast.makeText(this, "new image", Toast.LENGTH_SHORT).show();
                UplaodImage(ImageUri);
            }
            else
            {
                Toast.makeText(this, "same image", Toast.LENGTH_SHORT).show();
                downloadImageURL=oldImageUri.toString();
                loadingBar.dismiss();
                SaveProductInfoToDatabase();
            }


        }
        else
        { // new
            productRandomKey = saveCurrentDate + saveCurrentTime;
            UplaodImage(ImageUri);
        }


    }

    private void UplaodImage(Uri ImageUri)
    {
        final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey +".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText(NewProductActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(NewProductActivity.this, "תמונה הועלאה בהצלחה", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageURL=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageURL=task.getResult().toString();
                            Toast.makeText(NewProductActivity.this, "תמונה נשמרה בהצלחה", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });


}

    private void SaveProductInfoToDatabase()
    {
        HashMap<String,Object> productMap =  new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("name",ProductName);
        productMap.put("description",ProuctDescription);
        productMap.put("price",ProductPrice);
        productMap.put("image",downloadImageURL);



        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(NewProductActivity.this,HomeActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(NewProductActivity.this, "מוצר הוסף בהצלחה", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(NewProductActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

    }
}
