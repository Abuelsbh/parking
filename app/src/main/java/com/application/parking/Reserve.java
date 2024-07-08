package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class Reserve extends AppCompatActivity {

    MaterialSpinner From,To;
    Button Reserve;
    TextView Price;
    String[] Interval = new String[24];
    String Key;
    int numOfParking, price;
    String time1 = "0:00";
    String time2 = "0:00";
    CalendarView calendar;
    String Time1, Time2;
    String temp_key;
    String Date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    DatabaseReference database, database2;
    float TotalPrice = 0;
    BookingInfo bookingInfo = new BookingInfo();
    String Phone, Username;

    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        Phone = getIntent().getExtras().getString("Phone");
        Username = getIntent().getExtras().getString("Username");
        Key = getIntent().getExtras().getString("Key");
        numOfParking = getIntent().getExtras().getInt("numOfParking");
        price = getIntent().getExtras().getInt("Price");

        database = FirebaseDatabase.getInstance().getReference().child("Parking").child(Key).child("Bookings");
        database2 = FirebaseDatabase.getInstance().getReference().child("Users");

        From = findViewById(R.id.From);
        To = findViewById(R.id.To);
        Price = findViewById(R.id.Price);
        Reserve = findViewById(R.id.Reserve);
        calendar = findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Date = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });

        for(int i=0; i<24; i++)
            Interval[i] = String.valueOf(i)+":00";

        From.setAdapter(new MyAdapter(Reserve.this, R.layout.spinner, Interval));
        From.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                done = true;
                time1 = "";
                for(int i=0; i<Interval[position].length(); i++) {
                    if(Interval[position].charAt(i) == ':')
                        break;
                    time1 += Interval[position].charAt(i);
                }
                Time1 = Date + " " + time1 + ":00:00";
                Time2 = Date + " " + time2 + ":00:00";
                Date dateFrom = null;
                try {
                    dateFrom = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(Time1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dateTo = null;
                try {
                    dateTo = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(Time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TotalPrice = price * printDifference(dateFrom, dateTo);
                if (TotalPrice < 0) {
                    Price.setText("Total price is ");
                } else {
                    Price.setText("Total price is " + String.valueOf(TotalPrice));
                }
            }
        });

        To.setAdapter(new MyAdapter(Reserve.this, R.layout.spinner, Interval));
        To.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                done = true;
                time2 = "";
                for(int i=0; i<Interval[position].length(); i++) {
                    if(Interval[position].charAt(i) == ':')
                        break;
                    time2 += Interval[position].charAt(i);
                }
                Time1 = Date + " " + time1 + ":00:00";
                Time2 = Date + " " + time2 + ":00:00";
                Date dateFrom = null;
                try {
                    dateFrom = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(Time1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dateTo = null;
                try {
                    dateTo = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(Time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TotalPrice = price * printDifference(dateFrom, dateTo);
                if (TotalPrice < 0) {
                    Price.setText("Total price is ");
                } else {
                    Price.setText("Total price is " + String.valueOf(TotalPrice));
                }
            }
        });

        Random rand = new Random();
        String freeSlot = "Slot"+ rand.nextInt(numOfParking);

        Reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (done) {

                    java.util.Map<String, Object> map = new HashMap<>();
                    temp_key = database.child(freeSlot).push().getKey();
                    database.child(freeSlot).updateChildren(map);

                    DatabaseReference user = database.child(freeSlot).child(temp_key);
                    java.util.Map<String, Object> map2 = new HashMap<>();
                    map2.put("From", Time1);
                    map2.put("To", Time2);
                    map2.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    map2.put("Total Price", TotalPrice);
                    user.updateChildren(map2);

                    database2.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().child("Bookings").child(temp_key).child("Slot").setValue(freeSlot);
                                        dataSnapshot.getRef().child("Bookings").child(temp_key).child("Key").setValue(temp_key);
                                        dataSnapshot.getRef().child("Bookings").child(temp_key).child("KeyOfParking").setValue(Key);
                                        dataSnapshot.getRef().child("Bookings").child(temp_key).child("Parking").setValue("Done");
                                        Toast.makeText(Reserve.this, "Done you reserve the parking", Toast.LENGTH_SHORT).show();
                                        bookingInfo.setKeyOfParking(Key);
                                        bookingInfo.setKeyOfBooking(temp_key);
                                        bookingInfo.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        bookingInfo.setSlotNum(freeSlot);
                                        bookingInfo.setDateFrom(Time1);
                                        bookingInfo.setDateTo(Time2);
                                        bookingInfo.setPrice(TotalPrice);
                                        bookingInfo.setName(dataSnapshot.child("Username").getValue(String.class));
                                        bookingInfo.setPhone(dataSnapshot.child("Phone").getValue(String.class));
                                        File file = new File(Reserve.this.getExternalCacheDir(), File.separator + "invoice.pdf");
                                        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(bookingInfo, file);
                                        invoiceGenerator.create();
                                        invoiceGenerator.uploadFile(Reserve.this);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
                else {
                    Toast.makeText(Reserve.this, "Please select time first", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }



    public class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.spinner, parent, false);

            TextView textView = (TextView) layout.findViewById(R.id.text);

            textView.setText(String.valueOf(Interval[position]));

            return layout;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    public float printDifference(Date startDate, Date endDate) {

        float different = (float) endDate.getTime() - startDate.getTime();
        long hoursInMilli = 1000 * 60 * 60;

        float elapsedHours =(float)  (different / hoursInMilli);

        return elapsedHours;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }
}