package com.msdesign.fitbradford;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {
	private TextView txtName;
	private TextView txtEmail;
	private SQLiteHandler db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.userprofile_fragment, container, false);

		txtName = (TextView) v.findViewById(R.id.name);
		txtEmail = (TextView) v.findViewById(R.id.email);

		// SqLite database handler
		db = new SQLiteHandler(getActivity());

		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");

		// Displaying the user details on the screen
		txtName.setText(name);
		txtEmail.setText(email);

		return v;
	}
}