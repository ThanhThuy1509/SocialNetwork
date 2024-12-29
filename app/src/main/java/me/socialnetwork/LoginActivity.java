package me.socialnetwork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class LoginActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String cookie = getData(this, "cookie");
        Log.i("Cookie", cookie);
        if (!cookie.isEmpty()) {
            StandardAPI.getService.authLoginCookie(cookie).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) return;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    saveData(LoginActivity.this, "cookie", response.headers().get("Set-Cookie"));
                    assert response.body() != null;
                    saveData(LoginActivity.this, "id", response.body().getId());
                    saveData(LoginActivity.this, "avatar", response.body().getAvatar());
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {

                }
            });
        }

        Button loginButton = findViewById(R.id.loginButton);
        EditText username = findViewById(R.id.usernameInput);
        EditText password = findViewById(R.id.passwordInput);

        loginButton.setOnClickListener(v -> {
            Map<String, String> body = new HashMap<>();
            body.put("username", String.valueOf(username.getText()));
            body.put("password", String.valueOf(password.getText()));

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new JSONObject(body).toString());
            Log.i("LoginActivity", body.toString());

            StandardAPI.getService.authLogin(requestBody).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        saveData(LoginActivity.this, "cookie", response.headers().get("Set-Cookie"));
                        assert response.body() != null;
                        saveData(LoginActivity.this, "id", response.body().getId());
                        saveData(LoginActivity.this, "avatar", response.body().getAvatar());
                        startActivity(intent);
                    } else {
                        Log.i("Login response", "Failed");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    throwable.printStackTrace();
                }

            });
        });

    }
    public static void clearData(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sharedPreferences.edit().clear().commit();
    }

    public static void saveData(Context context, String key, String data) {
        if (data == null) {
            return;
        }

        // Save in the preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static String getData(Context context, String key)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        SharedPreferences.Editor edit = sharedPreferences.edit();
//        edit.clear();
//        edit.commit();
//        if (cookie.contains("expires")) {
//            edit.remove("cookie");
//            return "";
//        }
//        System.out.println(cookie);
        return sharedPreferences.getString(key, "");
    }

}