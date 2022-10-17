package com.example.firebasememoryapp;
// dont add any more code, she said to just have this i think

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private FirebaseFirestore db;
    private ArrayList<Memory> myMemories;
// will refer to all Memory objects for authorized user

    public final String TAG = "Denna";
    private static String uid = null;      // var will be updated for currently signed in user
    private FirebaseAuth mAuth;


    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myMemories = new ArrayList<>();
    }


    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void logOutUser() {
        mAuth.signOut();
        this.uid = null;
    }


    public void updateUid(String uid) {
        this.uid = uid;
    }

    public void attachReadDataToUser() {
        // This is necessary to avoid the issues we ran into with data displaying before it
        // returned from the asynch method calls
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getUid();
            readData(new FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<Memory> memoryList) {
                    Log.d(TAG, "Inside attachReadDataToUser, onCallback " + memoryList.toString());
                }
            });
        }
        else {
            Log.d(TAG, "No one logged in");
        }
    }

    public void addUserToFirestore(String name, String newUID) {
        // Create a new user with their name
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        // Add a new document with a docID = to the authenticated user's UID
        db.collection("users").document(newUID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, name + "'s user account added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user account", e);
                    }
                });
    }

    public void addData(Memory m) {
        // add Memory m to the database
        // this method is overloaded and incorporates the interface to handle the asynch calls
        addData(m, new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Memory> myList) {
                Log.i(TAG, "Inside addData, onCallback :" + myMemories.toString());
            }
        });
    }


    private void addData(Memory m, FirestoreCallback firestoreCallback) {
        db.collection("users").document(uid).collection("myMemoryList")
                .add(m)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // This will set the docID key for the Memory that was just added.
                        db.collection("users").document(uid).collection("myMemoryList").
                                document(documentReference.getId()).update("docID", documentReference.getId());
                        Log.i(TAG, "just added " + m.getName());
                        readData(firestoreCallback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error adding document", e);
                    }
                });
    }


    public ArrayList<Memory> getMemoryArrayList() {
        return myMemories;
    }



/* https://www.youtube.com/watch?v=0ofkvm97i0s
This video is good!!!   Basically he talks about what it means for tasks to be asynchronous
and how you can create an interface and then using that interface pass an object of the interface
type from a callback method and access it after the callback method.  It also allows you to delay
certain things from occurring until after the onSuccess is finished.
*/

    private void readData(FirestoreCallback firestoreCallback) {
        myMemories.clear();        // empties the AL so that it can get a fresh copy of data
        db.collection("users").document(uid).collection("myMemoryList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc: task.getResult()) {
                                Memory memory = doc.toObject(Memory.class);
                                myMemories.add(memory);
                            }

                            Log.i(TAG, "Success reading data: "+ myMemories.toString());
                            firestoreCallback.onCallback(myMemories);
                        }
                        else {
                            Log.d(TAG, "Error getting documents: " + task.getException());
                        }
                    }
                });
    }


    //https://stackoverflow.com/questions/48499310/how-to-return-a-documentsnapshot-as-a-result-of-a-method/48500679#48500679
    public interface FirestoreCallback {
        void onCallback(ArrayList<Memory> myList);
    }




}
