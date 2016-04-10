package com.msdesign.fitbradford;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();
	private Button btnLogin;
	private Button btnLinkToRegister;
	private EditText inputEmail;
	private EditText inputPassword;
	private ProgressDialog pDialog;
	private SessionManager session;
	private SQLiteHandler db;
	private String email;
	private String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		// SQLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// Session manager
		session = new SessionManager(getApplicationContext());

		// Check if user is already logged in or not
		if (session.isLoggedIn()) {
			// User is already logged in. Take him to main activity
			Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
			startActivity(intent);
			finish();
		}

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				email = inputEmail.getText().toString().trim();
				password = inputPassword.getText().toString().trim();
				login();
			} 
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});

	}

	/**
	 * Function to verify login details in MySQL database.
	 */
	private void checkLogin(final String email, final String password) {
		// Tag used to cancel the request
		String tag_string_req = "req_login";

		pDialog.setMessage("Authenticating..");
		showDialog();

		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, "Login Response: " + response.toString());
				hideDialog();

				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");

					// Check for error node in JSON
					if (!error) {
						// User successfully logged in
						// Create login session
						session.setLogin(true);
 
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
 
                        // Inserting row in users table
                        db.addUser(name, email);

						// Launch main activity
						Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
						startActivity(intent);
						finish();
					} else {
						// Error in login. Get the error message
						String errorMsg = jObj.getString("error_msg");
						Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// JSON error
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Login Error: " + error.getMessage());
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				hideDialog();
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login URL
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", email);
				params.put("password", password);
				return params;
			}
		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	
    public void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
        	Toast.makeText(getBaseContext(), "Enter valid credentials!", Toast.LENGTH_LONG).show();
            return;
        } else {
        	ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        	Boolean isInternetAvailable = cd.isConnectingToInternet();
        	if (isInternetAvailable != true) {
        		Toast.makeText(getBaseContext(), "Please check your interent connection!", Toast.LENGTH_LONG).show();
        	} else {
        		checkLogin(email, password);
        	}
        }
    }
    
	public boolean validate() {
		boolean valid = true;

	    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
	    	inputEmail.setError("Enter a valid email address");
	        valid = false;
	    } else {
	        inputEmail.setError(null);
	    }

	    if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
	        inputPassword.setError("Between 4 and 10 alphanumeric characters");
	        valid = false;
	    } else {
	        inputPassword.setError(null);
	    }

	    return valid;
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}