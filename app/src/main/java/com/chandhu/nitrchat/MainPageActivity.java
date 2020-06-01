package com.chandhu.nitrchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mail, passWord;
    private FirebaseAuth mAuth;
    Button signup;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        textView = findViewById(R.id.textView);
        mail = findViewById(R.id.mail);
        passWord = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(this);
        textView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup:
                registerUser();


                break;
            case R.id.textView:
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }

    private void registerUser() {
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

        mAuth.createUserWithEmailAndPassword(mailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(mailId, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(MainPageActivity.this, "Authentication Successful.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Error", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(MainPageActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

    }































    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
}
