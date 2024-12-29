package me.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.api.User;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });

        EditText username = findViewById(R.id.username);
        EditText name = findViewById(R.id.name);
        EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirm_password);


        Button registerButton = findViewById(R.id.sign_up_button);

        registerButton.setOnClickListener(v -> {
            if (username.getText().toString().isEmpty() || name.getText().toString().isEmpty() ||
            password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, getResources().getString(R.string.confirm_password), Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> body = new HashMap<>();
            body.put("username", String.valueOf(username.getText()));
            body.put("name", name.getText().toString());
            body.put("password", String.valueOf(password.getText()));

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new JSONObject(body).toString());

            StandardAPI.getService.authLRegister(requestBody).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        LoginActivity.saveData(RegisterActivity.this, "cookie", response.headers().get("Set-Cookie"));
                        assert response.body() != null;
                        LoginActivity.saveData(RegisterActivity.this, "id", response.body().getId());
                        LoginActivity.saveData(RegisterActivity.this, "avatar", response.body().getAvatar());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {

                }
            });
        });
    }
}