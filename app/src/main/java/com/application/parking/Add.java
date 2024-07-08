package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Add extends AppCompatActivity {

    EditText Title, NumOfParking, Price, About;
    Button Map, btn;
    RoundedImageView ProfilePicture,UploadImage;
    String temp_key;
    DatabaseReference root;
    ProgressDialog loadingBar;
    StorageReference MReference;
    Uri image = null,downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        root= FirebaseDatabase.getInstance().getReference().child("Parking");
        MReference = FirebaseStorage.getInstance().getReference().child("Parking");
        loadingBar=new ProgressDialog(this);

        Title = findViewById(R.id.Title);
        NumOfParking = findViewById(R.id.NumOfParking);
        Price = findViewById(R.id.Price);
        About = findViewById(R.id.About);
        ProfilePicture = findViewById(R.id.ProfilePicture);
        UploadImage = findViewById(R.id.UploadImage);
        Map = findViewById(R.id.Map);
        btn = findViewById(R.id.btn);

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 200);
            }
        });

        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add.this, Map.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
               /* File file = new File(Add.this.getExternalCacheDir(), File.separator + "invoice.pdf");
                InvoiceGenerator invoiceGenerator=new InvoiceGenerator("12",file);
                invoiceGenerator.create();
                invoiceGenerator.uploadFile(Add.this);
                invoiceGenerator.downloadFile(Add.this);
                invoiceGenerator.openFile(Add.this);*/
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Title.getText().toString().isEmpty() && !NumOfParking.getText().toString().isEmpty()
                        && !Price.getText().toString().isEmpty() && !About.getText().toString().isEmpty()
                        && image != null) {

                    loadingBar.setTitle("جارى حفظ الجراج");
                    loadingBar.setMessage("من فضلك انتظر لحين رفع بيانات الجراج...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    final StorageReference filepath = MReference.child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).child(image.getLastPathSegment());
                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    //uploading the image
                    UploadTask uploadTask = filepath.putBytes(data);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> download = taskSnapshot.getStorage().getDownloadUrl();
                            while (!download.isSuccessful()) ;
                            {
                                downloadUrl = download.getResult();
                            }

                            java.util.Map<String, Object> map1 = new HashMap<>();
                            temp_key = root.push().getKey();
                            root.updateChildren(map1);

                            java.util.Map<String, Object> map = new HashMap<>();
                            temp_key = root.push().getKey();
                            root.updateChildren(map);

                            DatabaseReference user = root.child(temp_key);

                            java.util.Map<String, Object> map2 = new HashMap<>();
                            map2.put("Location", Address);
                            map2.put("Long", Long);
                            map2.put("Lat", Lat);
                            map2.put("Title", Title.getText().toString());
                            map2.put("NumOfParking", Integer.parseInt(NumOfParking.getText().toString()));
                            map2.put("Price", Integer.parseInt(Price.getText().toString()));
                            map2.put("Image", downloadUrl.toString());
                            map2.put("Description", About.getText().toString());
                            map2.put("Owner", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            user.updateChildren(map2);
                            java.util.Map<String, Object> map3 = new HashMap<>();
                            for (int i=0; i<Integer.parseInt(NumOfParking.getText().toString()); i++) {
                                DatabaseReference Slot = root.child(temp_key).child("Bookings").child("Slot"+(i+1));
                                map3.put("Status", "Free");
                                Slot.updateChildren(map3);
                            }


                            loadingBar.dismiss();
                            Toast.makeText(Add.this, "تم اضافة الجراج", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Add.this, Home.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                            finish();

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
                else {
                    Toast.makeText(Add.this, "من فضلك ادخل جميع البيانات", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    String Address;
    Double Long, Lat;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            image = data.getData();
            ProfilePicture.setImageURI(image);
        }
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Address = data.getStringExtra("Address");
                Long = data.getDoubleExtra("Long",0);
                Lat = data.getDoubleExtra("Lat",0);
            }
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }
}