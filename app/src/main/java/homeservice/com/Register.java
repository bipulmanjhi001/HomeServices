package homeservice.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import homeservice.com.api.URLs;
import homeservice.com.model.CommonUtils;
import homeservice.com.model.ConnectionDetector;
import homeservice.com.model.VolleySingleton;

public class Register extends AppCompatActivity {

    EditText registerfirstname, registeraddress,registerphoneno,registeremailaddress,registerpassword,registerconfirmpassword;
    private String fname = null;
    private String mob_number = null;
    private String email=null;
    private String password=null;
    private String confirmpassword = null;
    Button registerbutton;
    NestedScrollView coordinatorlayout;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    Dialog myDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        coordinatorlayout=(NestedScrollView)findViewById(R.id.registercoordinatorlayout);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        registerfirstname=(EditText)findViewById(R.id.registerfirstname);
        registeraddress=(EditText)findViewById(R.id.registeraddress);
        registerphoneno=(EditText)findViewById(R.id.registerphoneno);
        registeremailaddress=(EditText)findViewById(R.id.registeremailaddress);
        registerpassword=(EditText)findViewById(R.id.registerpassword);
        registerconfirmpassword=(EditText)findViewById(R.id.registerconfirmpassword);
        registerbutton=(Button)findViewById(R.id.registerbutton);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        myDialog2=new Dialog(Register.this);

    }

    private void validateData() {
        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(registerfirstname.getText().toString()))
        {
            registerfirstname.setError("Required field!");
            focusView = registerfirstname;
            cancel = true;
        }
        else if(TextUtils.isEmpty(registerphoneno.getText().toString()))
        {
            registerphoneno.setError("Required field!");
            focusView = registerphoneno;
            cancel = true;
        }
        else if(TextUtils.isEmpty(registeremailaddress.getText().toString()))
        {
            registeremailaddress.setError("Required field!");
            focusView = registeremailaddress;
            cancel = true;
        }
        else if(TextUtils.isEmpty(registerpassword.getText().toString()))
        {
            registerpassword.setError("Required field!");
            focusView = registerpassword;
            cancel = true;
        }
        else if(TextUtils.isEmpty(registerconfirmpassword.getText().toString()))
        {
            registerconfirmpassword.setError("Required field!");
            focusView = registerconfirmpassword;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }
        else
        {
            getTextValues();
        }
    }

    private void getTextValues() {
        fname = registerfirstname.getText().toString();
        mob_number = registerphoneno.getText().toString();
        email = registeremailaddress.getText().toString();
        password = registerpassword.getText().toString();
        confirmpassword=registerconfirmpassword.getText().toString();

        if(CommonUtils.mobileNumberPatternMatcher(mob_number) && password.equals(confirmpassword)){
            if (isInternetPresent) {
                RegisterNow();
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorlayout, "Enable Internet!",
                        Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }
                        });

                TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action );
                snackbarActionTextView.setTextSize(14);
                snackbarActionTextView.setTextColor(Color.RED);
                snackbarActionTextView.setTypeface(snackbarActionTextView.getTypeface(), Typeface.BOLD);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setMaxLines(1);
                textView.setTextSize(14);
                textView.setSingleLine(true);
                textView.setTypeface(null, Typeface.BOLD);
                snackbar.show();
            }
        }
        else{
            Toast.makeText(this, "Please Enter a correct Mobile number or check password!", Toast.LENGTH_SHORT).show();
        }
    }

    public void RegisterNow(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("status")) {

                                registerfirstname.setText("");
                                registerphoneno.setText("");
                                registeremailaddress.setText("");
                                registerpassword.setText("");
                                registerconfirmpassword.setText("");

                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", fname);
                params.put("password", confirmpassword);
                params.put("mobile", mob_number);
                params.put("email", email);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}