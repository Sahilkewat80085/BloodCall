package gr.gdschua.bloodapp.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Alert;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;

public class EmergenciesAdapter extends ArrayAdapter<Alert> {
    private final Context mContext;
    private final List<Alert> alertList;
    DAOHospitals daoHospitals = new DAOHospitals();

    public EmergenciesAdapter(@NonNull Context context, ArrayList<Alert> list) {
        super(context, 0, list);
        mContext = context;
        alertList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.emergency_item, parent, false);

        TextView hospitalNameTV = listItem.findViewById(R.id.hospitalNameTV);
        TextView hospitalAddressTV = listItem.findViewById(R.id.hospitalAddressTV);
        TextView alertBloodTypeTV = listItem.findViewById(R.id.alertBloodTypeTV);

        // Get the alert object
        Alert currentAlert = alertList.get(position);

        // Fetch hospital data from Firebase
        daoHospitals.getUser(currentAlert.owner).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    if (snapshot.exists()) {
                        Hospital currHospital = snapshot.getValue(Hospital.class);

                        if (currHospital != null) {
                            hospitalNameTV.setText(currHospital.getName());
                            hospitalAddressTV.setText(currHospital.getAddress());
                            alertBloodTypeTV.setText(currentAlert.getBloodType());
                            Log.d("EmergenciesAdapter", "Hospital Loaded: " + currHospital.getName());
                        } else {
                            Log.e("EmergenciesAdapter", "Hospital object is null");
                        }
                    } else {
                        Log.e("EmergenciesAdapter", "No hospital data found for owner: " + currentAlert.owner);
                    }
                } else {
                    Log.e("EmergenciesAdapter", "Error fetching hospital data", task.getException());
                }
            }
        });

        return listItem;
    }
}
