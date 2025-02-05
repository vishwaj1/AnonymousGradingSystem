package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TintableCheckedTextView;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignInOptions;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout linearLayoutRegistration;
    private EditText username, email, password, confirmpassword;
    private Button signup, login;
    private AuthSignUpOptions options ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        signup = (Button)findViewById(R.id.SignUp);
        login = (Button)findViewById(R.id.Login);
        username = (EditText)findViewById(R.id.Username);
        email = (EditText)findViewById(R.id.Email);
        password = (EditText)findViewById(R.id.Password);
        confirmpassword = (EditText)findViewById(R.id.ConfirmPassword);

        signup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.SignUp){
            String name = username.getText().toString();
            String user_email = email.getText().toString();
            String user_password = password.getText().toString();
            String confirm = confirmpassword.getText().toString();
            signUp(name, user_email, user_password, confirm);
        }



    }

    private void signUp(String username, String email, @NonNull String password, String confirm) {
        if (!password.equals(confirm)) {
            showToast("Passwords do not match");
            return;
        }

        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.name(), username)
                .build();

        // Attempt to sign up the user
        Amplify.Auth.signUp(email, password, options,
                result -> {
                    runOnUiThread(() -> {
                        showConfirmationDialog(email);
                    });
                },
                error -> {
                    runOnUiThread(() -> {
                        Log.e("SignUpError", "Sign-up failed: " + error.toString());
                        showToast("Sign-up failed: " + error.getMessage());
                    });
                }
        );
    }
    private void showConfirmationDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Sign Up");
        builder.setMessage("A confirmation code was sent to " + email + ". Enter it below.");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirm", (dialog, which) -> confirmSignUp(email, input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }



    private void confirmSignUp(String email, String confirmationCode) {
        Amplify.Auth.confirmSignUp(
                email, confirmationCode,
                result -> runOnUiThread(() -> {
                    if (result.isSignUpComplete()) {
                        showToast("Sign-up successful!");
                        Intent intent = new Intent(Registration.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        showToast("Confirmation code is incorrect, please try again.");
                    }
                }),
                error -> runOnUiThread(() -> showToast("Failed to confirm sign up: " + error.getMessage()))
        );
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
