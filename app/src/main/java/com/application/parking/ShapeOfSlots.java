package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ShapeOfSlots extends AppCompatActivity {

    GridView gridView;
    ArrayList<Slot> Status = new ArrayList<>();
    ListViewAdaptor listViewAdaptor = new ListViewAdaptor();
    DatabaseReference database, database2;
    String Key;
    int numOfParking;
    Button btn, btn2;
    String temp_key;
    String freeSlot;
    String KeyOfBooking;
    String Parking;
    String KeyOfParking;
    BookingInfo bookingInfo = new BookingInfo();
    int Price;
    boolean done = true;
    String Phone, Username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_of_slots);

        Key = getIntent().getExtras().getString("Key");
        numOfParking = getIntent().getExtras().getInt("numOfParking");
        Price = getIntent().getExtras().getInt("Price");
        Phone = getIntent().getExtras().getString("Phone");
        Username = getIntent().getExtras().getString("Username");
        database = FirebaseDatabase.getInstance().getReference().child("Parking").child(Key).child("Bookings");
        database2 = FirebaseDatabase.getInstance().getReference().child("Users");
        gridView = findViewById(R.id.gridView);
        btn = findViewById(R.id.ReserveNow);
        btn2 = findViewById(R.id.ReserveLater);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Status = new ArrayList<>();
                listViewAdaptor = new ListViewAdaptor();
                for (int i=0; i<numOfParking; i++) {
                    if(snapshot.child("Slot"+(i+1)).child("Status").getValue(String.class).equalsIgnoreCase("Free")) {
                        Status.add(new Slot("Slot"+(i+1), false));
                    } else {
                        Status.add(new Slot("Slot"+(i+1), true));
                    }
                }
                listViewAdaptor.notifyDataSetChanged();
                gridView.setSelection(Status.size());
                gridView.setAdapter(listViewAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database2.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    if(dataSnapshot.child("Bookings").exists()) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.child("Bookings").getChildren()) {
                            if(dataSnapshot1.child("KeyOfParking").getValue(String.class).equalsIgnoreCase(Key)) {
                                KeyOfBooking = dataSnapshot1.child("Key").getValue(String.class);
                                KeyOfParking = dataSnapshot1.child("KeyOfParking").getValue(String.class);
                                if(dataSnapshot1.child("Parking").exists()) {
                                    Parking = dataSnapshot1.child("Parking").getValue(String.class);
                                }
                                freeSlot = dataSnapshot1.child("Slot").getValue(String.class);
                                if (Parking.equalsIgnoreCase("Now")) {
                                    btn.setText("Done");
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.getText().toString().equalsIgnoreCase("Reserve Now")) {
                    btn.setText("Done");
                    for (int i=0; i<Status.size(); i++) {
                        if(!Status.get(i).isStatus()) {
                            freeSlot = Status.get(i).getSlot();
                            break;
                        }
                    }
                    database.child(freeSlot).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(done) {
                                done = false;
                                snapshot.getRef().child("Status").setValue("Reserved");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    java.util.Map<String, Object> map = new HashMap<>();
                    temp_key = database.child(freeSlot).push().getKey();
                    database.child(freeSlot).updateChildren(map);

                    DatabaseReference user = database.child(freeSlot).child(temp_key);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    java.util.Map<String, Object> map2 = new HashMap<>();
                    map2.put("From", formatter.format(date));
                    map2.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    user.updateChildren(map2);
                    KeyOfBooking = temp_key;

                    database2.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                dataSnapshot.getRef().child("Bookings").child(temp_key).child("Parking").setValue("Now");
                                dataSnapshot.getRef().child("Bookings").child(temp_key).child("Slot").setValue(freeSlot);
                                dataSnapshot.getRef().child("Bookings").child(temp_key).child("Key").setValue(temp_key);
                                dataSnapshot.getRef().child("Bookings").child(temp_key).child("KeyOfParking").setValue(Key);
                                Toast.makeText(ShapeOfSlots.this, "Done you reserve the parking", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    btn.setText("Reserve Now");

                    database2.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.child("Bookings").getChildren()) {
                                            if (dataSnapshot1.child("Parking").getValue(String.class).equals("Now")) {
                                                KeyOfBooking = dataSnapshot1.child("Key").getValue(String.class);
                                                dataSnapshot.getRef().child("Bookings").child(KeyOfBooking).child("Parking").setValue("Done");
                                                bookingInfo.setKeyOfParking(KeyOfParking);
                                                bookingInfo.setKeyOfBooking(KeyOfBooking);
                                                bookingInfo.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                bookingInfo.setSlotNum(freeSlot);

                                                FirebaseDatabase.getInstance().getReference().child("Parking").child(KeyOfParking).child("Bookings")
                                                        .child(freeSlot).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (done) {
                                                                    done = false;
                                                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                                                    Date date = new Date();
                                                                    snapshot.getRef().child("Status").setValue("Free");
                                                                    Date dateFrom = null;
                                                                    try {
                                                                        dateFrom = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").
                                                                                parse(snapshot.child(KeyOfBooking).child("From").getValue(String.class));
                                                                        Toast.makeText(ShapeOfSlots.this, String.valueOf(dateFrom), Toast.LENGTH_SHORT).show();
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    bookingInfo.setDateFrom(snapshot.child(KeyOfBooking).child("From").getValue(String.class));
                                                                    snapshot.getRef().child(KeyOfBooking).child("To").setValue(formatter.format(date));
                                                                    bookingInfo.setDateTo(formatter.format(date));
                                                                    float TotalPrice = Price * printDifference(dateFrom, date);
                                                                    snapshot.getRef().child(KeyOfBooking).child("Total Price").setValue(TotalPrice);
                                                                    bookingInfo.setPrice(TotalPrice);
                                                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                                                            .orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                                        bookingInfo.setName(dataSnapshot.child("Username").getValue(String.class));
                                                                                        bookingInfo.setPhone(dataSnapshot.child("Phone").getValue(String.class));
                                                                                        File file = new File(ShapeOfSlots.this.getExternalCacheDir(), File.separator + "invoice.pdf");
                                                                                        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(bookingInfo, file);
                                                                                        invoiceGenerator.create();
                                                                                        invoiceGenerator.uploadFile(ShapeOfSlots.this);
                                                                                        /*Toast.makeText(ShapeOfSlots.this, "Done", Toast.LENGTH_SHORT).show();
                                                                                        Intent intent = new Intent(ShapeOfSlots.this, Evaluation.class);
                                                                                        intent.putExtra("KeyOfParking", Key);
                                                                                        startActivity(intent);
                                                                                        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);*/
                                                                                        break;
                                       /* invoiceGenerator.downloadFile(ShapeOfSlots.this);
                                        invoiceGenerator.openFile(ShapeOfSlots.this);*/
                                                                                    }

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                }

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShapeOfSlots.this, Reserve.class);
                intent.putExtra("Key",Key);
                intent.putExtra("numOfParking",numOfParking);
                intent.putExtra("Price",getIntent().getExtras().getInt("Price"));
                intent.putExtra("Username", Username);
                intent.putExtra("Phone",Phone);
                startActivity(intent);
                overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
            }
        });
    }


    public class ListViewAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return Status.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            View view1 = getLayoutInflater().inflate(R.layout.parking_slot, null);
            LinearLayout linear = view1.findViewById(R.id.linear);
            TextView txt = view1.findViewById(R.id.txt);

            txt.setText("Slot"+(i+1));
            if(Status.get(i).isStatus()) {
                linear.setBackgroundResource(R.drawable.slot_red);
            } else {
                linear.setBackgroundResource(R.drawable.slot_green);
            }



            return view1;
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