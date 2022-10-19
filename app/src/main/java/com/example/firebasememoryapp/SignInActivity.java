package com.example.firebasememoryapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SignInActivity extends AppCompatActivity  {

    // xml elements
    Button logInB, signUpB;
    EditText userNameET, passwordET;

    // values to pass to firebase auth for log in/sign up
    String userName, password;

    // this variable will be used for communicating with Firebase auth and firestore db
    public static FirebaseHelper firebaseHelper;

    // Use the same TAG all the time for Log statements. Feel free to change the value of TAG
    public final String TAG = "Denna";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseHelper = new FirebaseHelper();
        setContentView(R.layout.activity_sign_in);

        logInB = findViewById(R.id.logInButton);
        signUpB = findViewById(R.id.signUpButton);
        userNameET = findViewById(R.id.userNameEditText);
        passwordET = findViewById(R.id.passwordEditText);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI();
    }

    /**
     * This method will check to see if a user is already signed it to the app.
     * If a user is signed in, then they will be taken to SelectActionActivity
     * instead of loading this main screen
     */

    public void updateUI() {
        // if the user is already logged in, then they bypass this screen
        Log.d(TAG, "inside updateUI: " + firebaseHelper.getmAuth().getUid());
        if (firebaseHelper.getmAuth().getUid() != null) {
            firebaseHelper.attachReadDataToUser();
            Intent intent = new Intent(SignInActivity.this, SelectActionActivity.class);
            startActivity(intent);
        }
    }



    /**
     * Method first checks if the input is valid.  If it meets the screening criteria from
     * getValues(), then the username (which is an email) and password are sent to the FirebaseHelper
     * class to call upon a method of Firebase auth called createUserWithEmailAndPassword
     *
     * This method will return a result once it is complete, and we are listening for that result
     *
     * No matter what, the method will complete, either successfully or not
     * If successfully, that means the user account was created and we can now access the UID of
     * the signed in user.
     * After doing that we switch screens to the SelectActionActivity
     *
     * If unsuccessful, then we will Log that we failed, and include the exception message which will help
     * us gain insight into WHY it didn't work.
     *
     * @param view
     */


    public void signUpClicked(View view) {
        Log.i(TAG, "Sign up clicked");
        if (getValues()) {      // get username and password
            // Try to create an account using auth
            firebaseHelper.getmAuth().createUserWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                // Sign up successful, update UI with the currently signed in user's info
                                firebaseHelper.updateUid(firebaseHelper.getmAuth().getUid());
                                Log.d(TAG, userName + " created and logged in");

                                firebaseHelper.addUserToFirestore(userName, firebaseHelper.getmAuth().getUid());
                                firebaseHelper.attachReadDataToUser();

                                Intent intent = new Intent(SignInActivity.this, SelectActionActivity.class);
                                startActivity(intent);
                            }
                            else {
                                /*
                                This prevents the app from CRASHING when the user enters bad items
                                (duplicate email or badly formatted email most likely)

                                https://stackoverflow.com/questions/37859582/how-to-catch-a-firebase-auth-specific-exceptions

                                 */

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    // poorly formatted email address
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Sign up failed for " + userName + " " + password + e.getMessage());
                                } catch (FirebaseAuthEmailException e) {
                                    // duplicate email used
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Sign up failed for " + userName + " " + password + e.getMessage());
                                } catch (Exception e) {
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Sign up failed for " + userName + " " + password + e.getMessage());
                                }

                                // this log message will tell the name of the exception.  If you want to add this to the catch
                                // statement above, then just add another catch above the generic one at the end

                                Log.d(TAG, "Sign up failed for " + userName + " " + password +
                                        " because of \n"+ task.getException());
                            }
                        }
                    });
        }
        else {
            Log.d(TAG, "Failed to pass getValues() method");
        }
    }

    public void logInClicked(View view) {
        Log.i(TAG, "Log in clicked");
        if (getValues()) {        // get username and password
            // if valid, log in user and then switch to next activity
            // Try to sign into an account using auth with given email and password
            firebaseHelper.getmAuth().signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                // Sign in success, update currently signed in user's info
                                firebaseHelper.updateUid(firebaseHelper.getmAuth().getUid());
                                firebaseHelper.attachReadDataToUser();

                                Log.d(TAG, userName + " logged in");

                                Intent intent = new Intent(SignInActivity.this, SelectActionActivity.class);
                                startActivity(intent);
                            }
                            else {
                                 /*
                                This notifies the user of WHY they couldn't log in
                                https://stackoverflow.com/questions/37859582/how-to-catch-a-firebase-auth-specific-exceptions
                                 */

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    // wrong password
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Log in failed for " + userName + " " + password + e.getMessage());
                                } catch (FirebaseAuthInvalidUserException e) {
                                    // wrong email, no user found with this email
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Log in failed for " + userName + " " + password + e.getMessage());
                                } catch (Exception e) {
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Log in failed for " + userName + " " + password + e.getMessage());
                                }
                            }
                            // this log message will tell the name of the exception.  If you want to add this to the catch
                            // statement above, then just add another catch above the generic one at the end

                            Log.d(TAG, "Log in failed for " + userName + " " + password +
                                    " because of \n"+ task.getException());
                        }

                    });
        }
    }


    /**
     * Helper method to get userName and password whenever one of these buttons is clicked
     * The method makes sure both EditText boxes are filled in and also ensures the password
     * is at least 6 characters long.  If those tests pass, then it will send the values on
     * to be checked by Firebase auth
     *
     * @return true if values pass these tests, false otherwise
     */
    private boolean getValues() {
        userName = userNameET.getText().toString();
        password = passwordET.getText().toString();

        // verify all user data is entered
        if (userName.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // verify password is at least 6 char long (otherwise firebase will deny)
        else if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 char long", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.i(TAG, userName + " " + password + " is set after getValues(), return true");
            userName = removeTrailingSpaces(userName);
            return true;
        }
    }

    /**
     * This method accepts the email the user wants to submit for FirebaseAuth
     * and removes an extra spaces that may have accidentally been added at the end by
     * the auto-correct keyboard.  This typically happens when the email is used all
     * the time and shows up as a suggestion for the user.
     *
     * @param email
     * @return a String without trailing spaces
     */
    private String removeTrailingSpaces(String email) {
        String lastChar = email.substring(email.length() -1);
        if (lastChar.equals(" "))
            email = email.substring(0, email.length()-1);
        return email;
    }

}