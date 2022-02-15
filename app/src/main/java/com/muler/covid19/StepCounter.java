package com.muler.covid19;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class StepCounter extends Activity{
	TextView tv;
	double magnitudeprevi = 0;
	int stepcount = 0;
	int count = 0;
	Timer T;
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.step);
	   tv = (TextView)findViewById(R.id.steptext);
	   SensorManager sensormanager = (SensorManager)getSystemService(SENSOR_SERVICE);
	   Sensor sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	   SensorEventListener stepdetecter = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent sensorevent) {
			if(sensorevent !=null){
				float x_acceleration = sensorevent.values[0];
				float y_acceleration = sensorevent.values[1];
				float z_acceleration = sensorevent.values[2];
				double magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
			double magnitudedatta = magnitude - magnitudeprevi;
			magnitudeprevi = magnitude;
			if(magnitudedatta > 6){
				stepcount++;
			}
			////////////////////////
			T = new Timer();
		      T.scheduleAtFixedRate(new TimerTask() {         
		          @Override
		          public void run() {
		              runOnUiThread(new Runnable()
		              {
		                  @Override
		                  public void run()
		                  {

		                      count++;                
		                  }
		              });
		          }
		      }, 1000, 1000);
		      ////////////////////////////////////////////
		      if(count>200){
			tv.setText(""+stepcount);
		      }
			}
			
		}
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	sensormanager.registerListener(stepdetecter, sensor, sensormanager.SENSOR_DELAY_NORMAL);
	
	}
	@SuppressLint("NewApi") protected void onPouse(){
		super.onPause();
		SharedPreferences sharedpreference = getPreferences(MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedpreference.edit();
		editor.clear();
		editor.putInt("stepcount", stepcount);
		editor.apply();
	
		
	}
	@SuppressLint("NewApi") protected void onStop(){
		super.onStop();
		SharedPreferences sharedpreference = getPreferences(MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedpreference.edit();
		editor.clear();
		editor.putInt("stepcount", stepcount);
		editor.apply();
	}
	protected void onResume(){
		super.onResume();
		SharedPreferences sharedpreference = getPreferences(MODE_PRIVATE);
		stepcount = sharedpreference.getInt("stepcount", 0);
		
	}
}
