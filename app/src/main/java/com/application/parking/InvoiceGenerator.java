package com.application.parking;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceGenerator {
    // BookedSlots bookingSlot;
    //ParkingArea parkingArea;
    // User userObj;
    File file;
    //BasicUtils utils=new BasicUtils();
    BookingInfo bookingInfo;

    public InvoiceGenerator(){}

    public InvoiceGenerator(BookingInfo bookingInfo,File file){
        this.bookingInfo = bookingInfo;
        this.file=file;
    }


    public void create() {

        PdfDocument pdfDocument=new PdfDocument();
        Paint paint=new Paint();
        PdfDocument.PageInfo pageInfo=new PdfDocument.PageInfo.Builder(1000,725,1).create();
        PdfDocument.Page page=pdfDocument.startPage(pageInfo);
        Canvas canvas=page.getCanvas();

        paint.setTextSize(50);
        canvas.drawText("Smart Parking System",30,60,paint);

        paint.setTextSize(25);
        canvas.drawText("parkingArea.name",30,90,paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Invoice no",canvas.getWidth()-40,40,paint);
        canvas.drawText(bookingInfo.getKeyOfBooking(),canvas.getWidth()-40,80,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.rgb(150,150,150));
        canvas.drawRect(30,120,canvas.getWidth()-30,130,paint);

        paint.setColor(Color.BLACK);
        canvas.drawText("Date and time: ",50,170,paint);
        canvas.drawText(String.valueOf(bookingInfo.getDateFrom()),300,170,paint);


        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.rgb(150,150,150));
        canvas.drawRect(30,220,canvas.getWidth()-30,270,paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("Bill To: ",50,255,paint);
        canvas.drawText("User ID: ",450,255,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(bookingInfo.getEmail(),canvas.getWidth()-80,255,paint);

        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Customer Name: ",50,320,paint);
        canvas.drawText(bookingInfo.getName(),250,320,paint);
        canvas.drawText("Phone No: ",620,320,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(bookingInfo.getPhone(),canvas.getWidth()-50,320,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Email ID: ",50,365,paint);
        canvas.drawText(bookingInfo.getEmail(),180,365,paint);
        canvas.drawText("Slot No: ",620,365,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(bookingInfo.getSlotNum(),canvas.getWidth()-50,365,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.rgb(150,150,150));
        canvas.drawRect(30,415,canvas.getWidth()-30,465,paint);

        paint.setColor(Color.WHITE);
       /* canvas.drawText("Plate-Number",50,450,paint);
        canvas.drawText("Wheeler-Type",240,450,paint);*/
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Start-Time",170,450,paint);
        canvas.drawText("End-Time",canvas.getWidth()-150,450,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
       /* canvas.drawText("bookingSlot.numberPlate",50,495,paint);
        canvas.drawText(String.valueOf("bookingSlot.wheelerType"),240,495,paint);*/
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(bookingInfo.getDateFrom(),300,495,paint);
        canvas.drawText(bookingInfo.getDateTo(),canvas.getWidth()-50,495,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        paint.setColor(Color.rgb(150,150,150));
        canvas.drawRect(30,565,canvas.getWidth()-40,575,paint);

        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
//        canvas.drawText("",550,615,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Total Cost (Rs.):- "+String.valueOf(bookingInfo.getPrice()),canvas.getWidth()-50,615,paint);
        canvas.drawText("Paid:- Cash",canvas.getWidth()-50,660,paint);

        pdfDocument.finishPage(page);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }


    //this method will upload the file
    public void uploadFile(final Context context) {
        if(true) {
            //if there is a file to upload
            Uri filePath = Uri.fromFile(file);
            if (filePath != null) {
                //displaying a progress dialog while upload is going on

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference invoiceRef = storage.getReference().child("invoice/".concat(bookingInfo.getEmail()).concat("/").concat(bookingInfo.getKeyOfBooking()).concat(".pdf"));
                //StorageReference invoiceRef = storage.getReference().child(bookingInfo.getKeyOfBooking());
                invoiceRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                try {
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Uri downloadUrl;
                                Task<Uri> download = taskSnapshot.getStorage().getDownloadUrl();
                                while (!download.isSuccessful()) ;
                                {
                                    downloadUrl = download.getResult();
                                }
                                Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(context,MainActivity2.class);
                                intent.putExtra("Link", downloadUrl.toString());
                                intent.putExtra("KeyOfParking", bookingInfo.getKeyOfParking());
                                context.startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                try {
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            }
                        });
            }
            //if there is not any file
            else {
                //you can display an error toast
                Toast.makeText(context, "No file Available!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "No Network Available!", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadFile(Context context) {

       /* StorageReference  islandRef = FirebaseStorage.getInstance().getReference().child("invoice");

        File rootPath = new File(Environment.getExternalStorageDirectory(), bookingInfo.getEmail());
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,bookingInfo.getKeyOfBooking()+".pdf");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                Toast.makeText(context, "Download done", Toast.LENGTH_SHORT).show();
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });*/

        if (true) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            //StorageReference invoiceRef = storage.getReference().child(bookingInfo.getKeyOfBooking().concat(".pdf"));
            StorageReference invoiceRef = storage.getReference().child("invoice").child(bookingInfo.getEmail()).child(bookingInfo.getKeyOfBooking()+".pdf");

            final File localFile = new File(context.getExternalCacheDir(), File.separator + "invoice.pdf");
            invoiceRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ",";local tem file created  created " +localFile.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                    Log.e("firebase ",";local tem file not created  created " +exception.getMessage());
                }
            });
        }else{
            Toast.makeText(context, "No Network Available!", Toast.LENGTH_SHORT).show();
        }
    }

    public void openFile(Context context) {
        final File localFile = new File(context.getExternalCacheDir(), File.separator + "invoice.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(localFile),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    public void shareFile(Context context) {
        final File localFile = new File(context.getExternalCacheDir(), File.separator + "invoice.pdf");
        Intent share = new Intent(Intent.ACTION_SEND);
        if(localFile.exists()) {
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(localFile));
            share.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            share.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            context.startActivity(Intent.createChooser(share, "Share File"));
        }
    }
}
