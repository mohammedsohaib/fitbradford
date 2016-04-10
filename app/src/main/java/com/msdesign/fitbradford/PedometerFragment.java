package com.msdesign.fitbradford;

import com.msdesign.fitbradford.R;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PedometerFragment extends Fragment implements SensorEventListener {

	private SensorManager sensorManager;
	private TextView count;
	boolean activityRunning;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.pedometer_fragment, container, false);

		count = (TextView) v.findViewById(R.id.count);
		sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

		return v;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new PedometerFragment())
				.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		activityRunning = true;
		Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (countSensor != null) {
			sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
		} else {
			Toast.makeText(getActivity(), "Count sensor not available!", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		activityRunning = false;
		// if you unregister the last listener, the hardware will stop detecting
		// step events
		// sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (activityRunning) {
			count.setText(String.valueOf(event.values[0]));
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
