package com.dimatechs.ecartAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dimatechs.ecartAdmin.Model.Products;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class NewProductActivity extends AppCompatActivity {

    private Button AddNewProductBtn;
    private EditText EDProductName,EDProuctDescription,EdProductPrice;
    private ImageView ProductImage;
    private static final int GalleryPick=1;
    private Uri ImageUri,oldImageUri;
    private String ProductName ,ProuctDescription,ProductPrice,saveCurrentDate,saveCurrentTime,category;
    private String productRandomKey,downloadImageURL;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef,CategoryRef;
    private ProgressDialog loadingBar;
    private String productID="";
    private Spinner spinner1;
    Bitmap thumb_bitmap=null;
    byte[] thumb_byte=null;
    ByteArrayOutputStream byteArrayOutputStream;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        productID=getIntent().getStringExtra("pid");


        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef=FirebaseDatabase.getInstance().getReference().child("Products");
        CategoryRef=FirebaseDatabase.getInstance().getReference().child("Category");


        loadingBar=new ProgressDialog(this);
        spinner1=(Spinner) findViewById(R.id.spinner1);
        AddNewProductBtn=(Button)findViewById(R.id.Btn_new_product);
        EDProductName=(EditText) findViewById(R.id.Ed_Product_name);
        EDProuctDescription=(EditText) findViewById(R.id.Ed_Product_Description);
        EdProductPrice=(EditText) findViewById(R.id.Ed_Product_Price);
        ProductImage=(ImageView) findViewById(R.id.product_image);

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });

        AddNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

        //fill spinner
        Query query = CategoryRef.orderByChild("catName");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> catList = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String catName =dataSnapshot1.child("catName").getValue(String.class);
                    catList.add(catName);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewProductActivity.this,android.R.layout.simple_spinner_item,catList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewProductActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "image crop", Toast.LENGTH_SHORT).show();
                ImageUri = result.getUri();

                File thumb_filePathUri = new File(ImageUri.getPath());
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);

                    byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                    thumb_byte = byteArrayOutputStream.toByteArray();

                    Toast.makeText(this, "image compress", Toast.LENGTH_SHORT).show();

                    ProductImage.setImageURI(ImageUri);
                    Toast.makeText(this, "image ImageUri" + ImageUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        loadingBar.setTitle("שמירת פרטי מוצר");
        loadingBar.setMessage("המתן בבקשה");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        category = spinner1.getSelectedItem().toString();

        if(productID !=null)
        { //update
            productRandomKey=productID;

            if(ImageUri !=null && ImageUri.toString() != oldImageUri.toString() )
            {
                //new image

                UploadImage();
            }
            else
            {
                //same image
                downloadImageURL=oldImageUri.toString();
                loadingBar.dismiss();
                SaveProductInfoToDatabase();
            }


        }
        else
        { // new product
            productRandomKey = saveCurrentDate + saveCurrentTime;
            UploadImage();
        }


    }

    private void UploadImage()
    {
       // final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey +".jpg");
        final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey +".jpg");

      //  final UploadTask uploadTask = filePath.putFile(resultUri);
        final UploadTask uploadTask=filePath.putBytes(thumb_byte);

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
        productMap.put("category",category);

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
                            Toast.makeText(NewProductActivity.this, "מוצר נשמר בהצלחה", Toast.LENGTH_SHORT).show();
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
