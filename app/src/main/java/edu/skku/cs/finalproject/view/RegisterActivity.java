package edu.skku.cs.finalproject.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.RegisterModel;
import edu.skku.cs.finalproject.presenter.RegisterPresenter;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText idText =findViewById(R.id.registerIDInput);
        EditText pwText =findViewById(R.id.registerPWInput);
        EditText pwAgainText = findViewById(R.id.registerPWAgainInput);
        EditText emailText =findViewById(R.id.registerEmailInput);
        EditText wonText=findViewById(R.id.registerWonInput);
        EditText bitText=findViewById(R.id.registerBitInput);
        EditText usdText=findViewById(R.id.registerUsdInput);

        Button registerBtn=findViewById(R.id.registerFinBtn);
        RegisterPresenter registerPresenter = new RegisterPresenter(this);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pwText.getText().toString().equals(pwAgainText.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                   registerPresenter.btnClicked(idText.getText().toString(),pwText.getText().toString(),emailText.getText().toString(),wonText.getText().toString(),bitText.getText().toString(),usdText.getText().toString());
                }
            }
        });


    }

    public void registerFin() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
