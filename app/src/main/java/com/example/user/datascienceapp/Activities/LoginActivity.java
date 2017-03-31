package com.example.user.datascienceapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.datascienceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Performs login function
 * If the user is new it sign ups instead of logging in
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText fname,lname,school,grade;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String schoolName="test";
    ArrayList<Integer> imageNo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        fname= (EditText) findViewById(R.id.fname);
        lname= (EditText) findViewById(R.id.lname);
        school= (EditText) findViewById(R.id.school);
        grade= (EditText) findViewById(R.id.grade);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageNo=getIntent().getIntegerArrayListExtra("imageNo");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if(school.getText().toString()!=null)
                schoolName=school.getText().toString();

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //Logging out if user is annonymous
                    if(user.isAnonymous()){
                        Log.d("User","Annonymous"+user.getUid());
                        firebaseAuth.signOut();
                    }
                    else {
                        String uid=user.getUid();
                        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=firebaseDatabase.getReference("user").child(uid).child("school");
                        databaseReference.setValue(schoolName);
                        Intent intent = new Intent(getBaseContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                        Log.d("Tag", "onAuthStateChanged:signed_in:" + user.getUid());
                    }
                }
                else {
                    // User is signed out
                    Log.d("Tag", "onAuthStateChanged:signed_out at Login");
                }
            }
        };

    }
    @OnClick(R.id.sign_up_button)
    void login(View view){


        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String f_name=fname.getText().toString()+" ";
        String l_name=lname.getText().toString()+" ";
        String grade_name=grade.getText().toString();

        // Prevent empty fields
        if(f_name.equals("")||l_name.equals("")||grade_name.equals("")){
            Toast.makeText(getBaseContext(),"Fill all the fields",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else {
            if(grade_name.equals("Below 5"))
                grade_name="0";
            else if(grade_name.equals("Above 12"))
                grade_name="13";
            /**
             * Firebase accpets username as emails only. Custom email is formed for each user using his/her firstname, lastname and grade.
             * Default password is used for all users.
             */
            final String email = f_name.substring(0,f_name.indexOf(' ')) + "." + grade_name + "@" + l_name.substring(0,l_name.indexOf(' ')) + ".com";
            final String password = "password123"; // default password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Tag", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                if(mAuth.getCurrentUser()!=null)
                                    mAuth.signOut();
                                login(email, password);  // Old User
                                Log.d("Old","User");
                            }
                            else{
                                loginAnno(email,password); //New User
                                Log.d("New","User");
                            }
                        }
                    });
        }

    }
    @OnClick(R.id.grade)
    void dialog(View view){
        final CharSequence grades[] = new CharSequence[] {"Below 5","5th","6th","7th","8th","9th","10th","11th","12th","Above 12"};
        // alert dialog box  to choss the grade
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle("Choose Grade");
        builder.setItems(grades, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    grade.setText(grades[which]);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //the user clicked on Cancel
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        builder.show();
    }
    @Override
    public void onStop() {
        super.onStop();
       if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Merging the account with the anonymous account
     * @param email - it has he format as- firstname.grage@lastname.com
     * @param password - default password 
     */
    public void loginAnno(String email,String  password){
        AuthCredential credential=EmailAuthProvider.getCredential(email,password);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "linkWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    // Signing In a old user
    public void login(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
         @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {
                    Toast.makeText(getBaseContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else{

                }
            }
});
    }
    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }


}
