package com.example.firebasememoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMemoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    // How to implement a Spinner
    // https://www.tutorialspoint.com/how-to-get-spinner-value-in-android

    // How to style the spinner
    // https://www.youtube.com/watch?v=7tnlh1nVkuE

    public final String TAG = "Denna";

    Spinner spinner;
    EditText memoryName, memoryDesc;
    String spinnerSelectedText = "none";
    int memoryRatingNum;


    // Necessary to upload an image

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        memoryName = findViewById(R.id.memNameEditText);
        memoryDesc = findViewById(R.id.memoryDescEditText);

        db = SignInActivity.firebaseHelper.getDb();

        // this attaches my spinner design (spinner_list.xml) and my array of spinner choices(R.array.memoryRating)
        spinner = findViewById(R.id.memorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_list,
                getResources().getStringArray(R.array.memoryRating));

        // this attaches my custom row design (how I want each row to look)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_row);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerSelectedText = parent.getItemAtPosition(position).toString();
        // if string array is only numbers, you can do this below.
        // my array had words in it, so I need the extra for loop you will see below.
        // that is why this is commented out FOR ME
        // memoryRatingNum = Integer.parseInt(spinnerSelectedText);

        Toast.makeText(parent.getContext(), spinnerSelectedText, Toast.LENGTH_SHORT).show();
    }
    // This method is required, even if empty, for the OnItemSelectedListener to work
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        spinnerSelectedText = "none selected";
    }

    public void goBackButtonClicked(View view) {
        Intent intent = new Intent(AddMemoryActivity.this, SelectActionActivity.class);
        startActivity(intent);
    }

    public void addMemoryButtonClicked(View view) {
        String memName = memoryName.getText().toString();
        String memDesc = memoryDesc.getText().toString();


        //*** If you only have numbers in your string array, then don't include this part! *** //

        //*** IF you have String text in your string array, then use a loop such as this ***//

        // This loop take the option they clicked on and ensure it is a number.
        // My options went from 5 to 1, so that is why I have it adjusted with 6-i
        // I also had an instruction statement as my first line in my string array
        // ADJUST THIS LOOP TO MATCH YOUR CODE!

        // Note the syntax here for how to access an index of a string array within java
        for (int i = 1; i < 6; i++) {
            if (spinnerSelectedText.equals(getResources().getStringArray(R.array.memoryRating)[i])) {
                memoryRatingNum = 6-i;
                break;
            }
        }

        // Everyone needs this part below this comment

        Memory m = new Memory(memoryRatingNum, memName, memDesc);
        SignInActivity.firebaseHelper.addData(m);

        memoryName.setText("");
        memoryDesc.setText("");
    }



}