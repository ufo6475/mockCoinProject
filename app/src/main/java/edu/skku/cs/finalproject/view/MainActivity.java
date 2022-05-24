package edu.skku.cs.finalproject.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = (Button)findViewById(R.id.loginButton);

        MainPresenter mainPresenter = new MainPresenter(this);

        EditText idText =findViewById(R.id.loginIDInput);
        EditText pwdText = findViewById(R.id.loginPWInput);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.loginClicked(idText.getText().toString(),pwdText.getText().toString());
            }
        });

        Button registerBtn= findViewById(R.id.loginRegisterButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });




    }
}