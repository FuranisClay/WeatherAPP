package com.furan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.furan.MainActivity;
import com.furan.R;
import com.furan.database.UserDatabaseHelper;
import com.furan.model.UserSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText edtName, edtEmail;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "请输入用户名和邮箱", Toast.LENGTH_SHORT).show();
                return;
            }

            String loginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            new UserDatabaseHelper(this).loginUser(name, email, loginTime);

            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            UserSession.setCurrentUserName(name);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
