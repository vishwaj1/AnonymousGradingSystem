package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Action;
import com.amplifyframework.core.Consumer;
import com.amplifyframework.auth.AuthException;


import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.example.myapplication.AddClassActivity;
import com.example.myapplication.R;
import com.example.myapplication.Registration;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected Button ButtonLogin,  ButtonSignUp;
    protected EditText email, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonLogin = (Button) findViewById(R.id.Login);
        ButtonSignUp = (Button) findViewById(R.id.SignUp);
        email = (EditText) findViewById(R.id.LoginId);
        Password = (EditText) findViewById(R.id.PasswordEditText);

        ButtonLogin.setOnClickListener(v -> checkAndSignOutBeforeSignIn());
        ButtonSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.SignUp){
            Intent intent = new Intent(MainActivity.this, Registration.class);
            startActivity(intent);


        }
    }



    private void signIn(String username, String password) {
        Amplify.Auth.signIn(username, password,
                result -> {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(MainActivity.this, AddClassActivity.class);
                        startActivity(intent);
                    });
                },
                error -> {
                    runOnUiThread(() -> {
                        Log.e("LoginError", error.toString());  // Log the full error

                        //showToast("Username or Password are incorrect");
                    });
                }
        );
    }


    private void checkAndSignOutBeforeSignIn() {
        Amplify.Auth.fetchAuthSession(
                session -> {
                    //performSignOut();
                    if (session.isSignedIn()) {
                        // If signed in, proceed to sign out
                        performSignOut();
                        String emailId = email.getText().toString();
                        String password = Password.getText().toString();
                        signIn(emailId,password);
                    } else {
                        // Directly proceed to sign in if not signed in
                        String emailId = email.getText().toString();
                        String password = Password.getText().toString();
                        signIn(emailId,password);
                    }
                },
                error -> Log.e("AuthSession", "Error fetching auth session", error)
        );
    }


    private void performSignOut() {
        AuthSignOutOptions options = AuthSignOutOptions.builder()
                .globalSignOut(true)
                .build();

        Amplify.Auth.signOut(options, signOutResult -> {
            if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                // handle successful sign out
                Log.i("signout","Signout successful");
            }
        });
    }




    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}