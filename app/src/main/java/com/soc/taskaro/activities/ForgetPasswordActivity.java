package com.soc.taskaro.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.soc.taskaro.R;
import com.soc.taskaro.utils.Extras;

public class ForgetPasswordActivity extends AppCompatActivity {

    Button btn_send;
    TextView goToLoginTextView, forgotPasswordTagline;
    ProgressDialog progressDialog;
    EditText txt_email;
    boolean isEmailValid;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btn_send = findViewById(R.id.btn_send);
        goToLoginTextView = findViewById(R.id.goToLoginTextView);
        txt_email = findViewById(R.id.txt_email);
        firebaseAuth = FirebaseAuth.getInstance();
        forgotPasswordTagline = findViewById(R.id.forgotPasswordTagline);

        // This fills the textView with gradient
        TextPaint paint = forgotPasswordTagline.getPaint();
        float width = paint.measureText(getString(R.string.forget_password));
        Shader textShader = new LinearGradient(0, 0, width, forgotPasswordTagline.getTextSize(),
                new int[]{
                        getResources().getColor(R.color.md_theme_light_shadow),
                        getResources().getColor(R.color.seed)
                }, null, Shader.TileMode.CLAMP);
        forgotPasswordTagline.getPaint().setShader(textShader);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean have_WIFI = false;
                boolean have_MobileData = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo info : networkInfos) {
                    if (info.getTypeName().equalsIgnoreCase("WIFI"))
                        if (info.isConnected())
                            have_WIFI = true;

                    if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (info.isConnected())
                            have_MobileData = true;
                }
                if (have_MobileData || have_WIFI) {
                    progressDialog = new Extras().showProgressBar(ForgetPasswordActivity.this);
                    SetValidation();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed! Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgetPasswordActivity.this, LoginScreenActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void SetValidation() {
        if (txt_email.getText().toString().isEmpty()) {
            txt_email.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txt_email.getText().toString()).matches()) {
            txt_email.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
        }

        if (isEmailValid) {
            String email_txt = txt_email.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(email_txt).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        TextView txt_goToLogin;
                        progressDialog.dismiss();
                        Dialog dialog = new Dialog(ForgetPasswordActivity.this);
                        dialog.setContentView(R.layout.check_email_pop);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                        txt_goToLogin = dialog.findViewById(R.id.txt_goToLogin);

                        // This fills the textView with gradient
                        TextPaint paint = txt_goToLogin.getPaint();
                        float width = paint.measureText(getString(R.string.back_to_login_underline));
                        Shader textShader = new LinearGradient(0, 0, width, txt_goToLogin.getTextSize(),
                                new int[]{
                                        getResources().getColor(R.color.md_theme_light_shadow),
                                        getResources().getColor(R.color.seed)
                                }, null, Shader.TileMode.CLAMP);
                        txt_goToLogin.getPaint().setShader(textShader);

                        txt_goToLogin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(ForgetPasswordActivity.this, LoginScreenActivity.class);
                                startActivity(i);
                                dialog.dismiss();
                                finish();
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed! You are not registered with this Email ID", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}