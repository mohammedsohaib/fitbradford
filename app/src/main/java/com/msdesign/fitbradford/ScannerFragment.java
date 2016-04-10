package com.msdesign.fitbradford;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScannerFragment extends Fragment implements OnClickListener {
	private Button scanBtn;
	private TextView contentTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.scanner_fragment, container, false);
		
		scanBtn = (Button) v.findViewById(R.id.scan_button);
		contentTxt = (TextView) v.findViewById(R.id.scan_content);

		// Set OnClickListenter to the scan button
		scanBtn.setOnClickListener(this);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.scan_button){
			IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
			scanIntegrator.initiateScan();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			// Get data from scanning
			String scanContent = scanningResult.getContents();
			// Show the data within the TextView
			contentTxt.setText("CONTENT: " + scanContent);
		} else {
			Toast.makeText(getActivity(), "No scan data received!", Toast.LENGTH_SHORT).show();
		}
	}
}
