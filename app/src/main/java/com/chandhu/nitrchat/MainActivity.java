package com.chandhu.nitrchat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 1;
    EditText mail, passWord;
    Button signIn;
    TextView textView;
    SignInButton google_button;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        signIn = findViewById(R.id.signin);
        passWord = findViewById(R.id.password);
        mail = findViewById(R.id.mail);
        google_button = findViewById(R.id.google_button);
        textView.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                            .requestEmail()
                                                            .requestIdToken(getString(R.string.default_web_client_id))
                                                            .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_button.setOnClickListener(this);

        getPermissions();
    }

    private void getPermissions() {
        requestPermissions(new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d("Login Successful", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("Log in Failed", "Google sign in failed", e);
                Toast.makeText(this, "Log in With Google Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),HomePage.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            finish();
            Intent intent = new Intent(getApplicationContext(),HomePage.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textView:
                finish();
                Intent intent = new Intent(getApplicationContext(),MainPageActivity.class);
                startActivity(intent);
            case R.id.signin:
                signInUser();
            case R.id.google_button:
                Toast.makeText(this, "Authentication begins", Toast.LENGTH_SHORT).show();
                signIn();
        }
    }

    private void signInUser() {
        final String mailId = mail.getText().toString();
        String password = passWord.getText().toString();

        if(mailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mailId).matches()){
            mail.setError("Enter a valid Email");
            mail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passWord.setError("Enter a valid Password");
            passWord.requestFocus();
            return;
        }

        if (password.length() < 6){
            passWord.setError("Minimum letters in password is 6");
            passWord.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(mailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Sign in Success", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(getApplicationContext(),HomePage.class));
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Signed In failure", "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
