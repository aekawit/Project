package com.example.voidcallwebserviceceltofahren;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;






import java.util.ArrayList;
import java.util.Locale;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


//ไม่ได้ใส่ตามตัวอย่างไป 1 บรรทัด
import android.os.AsyncTask;
//import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	
		
		private final String NAMESPACE = "http://www.w3schools.com/webservices/";
		private final String URL = "http://www.w3schools.com/webservices/tempconvert.asmx";
		private final String SOAP_ACTION = "http://www.w3schools.com/webservices/CelsiusToFahrenheit";
		private final String METHOD_NAME = "CelsiusToFahrenheit";
		private String TAG = "PGGURU";
		private static String celcius;
		private static String fahren;
		Button b;
		TextView tv;
		EditText et;
	
		
	//ส่วนของ ปุ่ม เสียงเป็นข้อความ ข้อความเป็นเสียง
	//		
		protected static final int RESULT_SPEECH = 1;
	    private ImageButton btnSpeak;
	    private TextView edText;
		
	    final static int INTENT_CHECK_TTS = 0;
	    TextToSpeech tts;
	//
	//
		
	
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
    	//ส่วนของ ปุ่ม เสียงเป็นข้อความ ข้อความเป็นเสียง	
		//		
		 	edText = (TextView) findViewById(R.id.editText); //เปลี่นนตอน อ่านค่า ฟาเรนไฮน์
	        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
	        btnSpeak.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
	                try {
	                    startActivityForResult(intent, RESULT_SPEECH);
	                    // tv.setText("");  //เปลี่นนตอน อ่านค่า ฟาเรนไฮน์
	                } catch (ActivityNotFoundException a) {
	                    Toast t = Toast.makeText(getApplicationContext(),
	                            "Opps! Your device doesn't support Speech to Text",
	                            Toast.LENGTH_SHORT);
	                    t.show();
	                }
	            }
	        });
	        
	        Intent intent = new Intent();
	        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	        startActivityForResult(intent, INTENT_CHECK_TTS);
	    //
	    //
	        

	}
	//ส่วนของ ปุ่ม เสียงเป็นข้อความ ข้อความเป็นเสียง	
	//


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	 super.onActivityResult(requestCode, resultCode, data);
    	 	
			et = (EditText) findViewById(R.id.editText);
			tv = (TextView) findViewById(R.id.tv_result);
			
        switch (requestCode) {
        case RESULT_SPEECH: {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edText.setText(text.get(0));
            	}
            
            if (et.getText().length() != 0 && et.getText().toString() != "") {
					celcius = et.getText().toString();
					AsyncCallWS task = new AsyncCallWS();
					task.execute();
					System.out.println(celcius);
				} else {
					tv.setText("กรุณากรอกค่า เซลเซียส");
				}
            
            
            break;
        	}
        }
        if (requestCode == INTENT_CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(MainActivity.this, new OnInitListener() {
                    public void onInit(int arg0) {
                        tts.setLanguage(Locale.US);
                    }
                });
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        
    }
    public void onDestroy() {
        super.onDestroy();
        if(tts != null)
            tts.shutdown();
    }
    //
    //
    
	
	public void getFahrenheit(String celsius) {
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		PropertyInfo celsiusPI = new PropertyInfo();
		celsiusPI.setName("Celsius");
		celsiusPI.setValue(celsius);
		celsiusPI.setType(double.class);
		request.addProperty(celsiusPI);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			fahren = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			Log.i(TAG, "doInBackground");
			getFahrenheit(celcius);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			Log.i(TAG, "onPostExecute");
			tv.setText(fahren + " Fahrenheit");
			
			String str = tv.getText().toString();
			tts.speak(str, TextToSpeech.QUEUE_ADD, null);
		}
		@Override
		protected void onPreExecute() {
			Log.i(TAG, "onPreExecute");
			tv.setText("กำลังคำนวณผล...");
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			Log.i(TAG, "onProgressUpdate");
		}
	}
			
}
