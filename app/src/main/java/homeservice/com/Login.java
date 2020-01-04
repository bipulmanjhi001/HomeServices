package homeservice.com;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import homeservice.com.api.URLs;
import homeservice.com.model.ConnectivityReceiver;
import homeservice.com.model.VolleySingleton;
import homeservice.com.pref.SharedPrefManager;
import homeservice.com.pref.User;

public class Login extends AppCompatActivity {

    EditText usernameEditText,passwordEditText;
    Button loginButton,register;
    String username,password;
    ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                finish();
                startActivity(new Intent(this, MapActivity.class));
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        usernameEditText = (EditText)findViewById(R.id.username);
        passwordEditText = (EditText)findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            attemptLogin();
        } else {
            message = "connect your internet.";
            color = Color.RED;
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    private void attemptLogin() {
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            focusView = usernameEditText;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_incorrect_password));
            focusView = passwordEditText;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();

        } else {
            Authenticate();
        }
    }

    public void Authenticate() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {

                                JSONObject userJson = obj.getJSONObject("user");
                                String token = userJson.getString("token");
                                String username = userJson.getString("mobile");

                                User user = new User(userJson.getString("token"),
                                        userJson.getString("mobile"));

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();

                                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                intent.putExtra("name", username);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                finish();

                            } else if (!obj.getBoolean("status")) {

                                String error = obj.getString("error");
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Connection error..", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", username);
                params.put("password", password);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
