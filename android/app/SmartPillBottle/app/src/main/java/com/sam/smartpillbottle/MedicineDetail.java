package com.sam.smartpillbottle;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

public class MedicineDetail extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Medication medication;
    private TextView medicineName;
    private TextView lastDose;
    private TextView nextDose;
    private TextView remainingPills;
    private TextView pillsPerDose;
    private Button deleteButton;
    private DatabaseReference firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseDatabase = MedicineList.getFirebaseDatabase();
        firebaseUser = MedicineList.getFirebaseUser();
        firebaseAuth = MedicineList.getFirebaseAuth();

        medication = MedicineList.getClickedMedication();

        medicineName = findViewById(R.id.detailMedicineNameLabel);
        lastDose = findViewById(R.id.detailLastDoseLabel);
        nextDose = findViewById(R.id.detailNextDoseLabel);
        remainingPills = findViewById(R.id.detailRemainingPillsLabel);
        pillsPerDose = findViewById(R.id.detailRequiredDoseLabel);

        medicineName.setText(medication.getName());
        remainingPills.setText("Remaining Pills: "  + medication.getRemainingPills());
        lastDose.setText("Last Dose: " + medication.getLastDose().toString());
        nextDose.setText("Next Dose: " + medication.getNextDose().toString());
        pillsPerDose.setText("Pills Per Dose: " + medication.getPillsPerDose());

        deleteButton = findViewById(R.id.detailDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog confirm = new Dialog(MedicineDetail.this);

                confirm.show();
                firebaseDatabase.child("users/" + firebaseUser.getUid() +"/medicine/" + medication.getMedicineUID()).removeValue();
                finish();
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng medicineLocation = new LatLng(Double.parseDouble(medication.getLatitude()), Double.parseDouble(medication.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(medicineLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(medicineLocation, 17));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17 ),2000,null);
    }


}
