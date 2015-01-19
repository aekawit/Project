package com.example.Getversion;

import java.util.ArrayList;
import java.util.Locale;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.Getversion.*;
import com.example.mainapp.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private final String NAMESPACE = "http://axisversion.sample";
	private final String URL = "http://192.168.50.177/axis2/services/Version.VersionHttpSoap11Endpoint";
	private final String SOAP_ACTION = "urn:getVersion";
	private final String METHOD_NAME = "getVersion";
	private String TAG = "aeke";
	private static String celcius;
	private static String fahren;
	Button b;

	TextView tv;
	EditText et;


	//��ǹ�ͧ ���� ���§�繢�ͤ��� ��ͤ��������§
	//		
		protected static final int RESULT_SPEECH = 1;
	    private ImageButton btnSpeak;
	    private TextView edText;
		
	    final static int INTENT_CHECK_TTS = 0;
	    TextToSpeech tts;
	    //EditText et;
	    // Button btn;    
	//
	//
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

      //��ǹ�ͧ ���� ���§�繢�ͤ��� ��ͤ��������§	
      		//		
      		 	edText = (TextView) findViewById(R.id.editText); //���蹹�͹ ��ҹ��� ���ù�ι�
      	        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
      	        btnSpeak.setOnClickListener(new View.OnClickListener() {
      	            public void onClick(View v) {
      	                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
      	                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
      	                try {
      	                    startActivityForResult(intent, RESULT_SPEECH);
      	                    // tv.setText("");  //���蹹�͹ ��ҹ��� ���ù�ι�
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
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    
    @Override
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
				//Get the text control value
				celcius = et.getText().toString();
				//Create instance for AsyncCallWS
				AsyncCallWS task = new AsyncCallWS();
				//Call execute 
				task.execute();
			//If text control is empty
				System.out.println(celcius);
			} else {
				tv.setText("��سҡ�͡��� ������");
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
    
    
    
    
	public void getFahrenheit(String celsius) {
		//Create request
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		//Property which holds input parameters
		PropertyInfo celsiusPI = new PropertyInfo();
		//Set Name
		celsiusPI.setName("Celsius");
		//Set Value
		celsiusPI.setValue(celsius);
		//Set dataType
		celsiusPI.setType(double.class);
		//Add the property to request object
		request.addProperty(celsiusPI);
		//Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		//Set output SOAP object
		envelope.setOutputSoapObject(request);
		//Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			//Invole web service
			androidHttpTransport.call(SOAP_ACTION, envelope);
			//Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			//Assign it to fahren static variable
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
			tv.setText(fahren);
			
			String str = tv.getText().toString();
            tts.speak(str, TextToSpeech.QUEUE_ADD, null);
		}
		@Override
		protected void onPreExecute() {
			Log.i(TAG, "onPreExecute");
			tv.setText("���ѧ�ӹǳ��...");
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			Log.i(TAG, "onProgressUpdate");
		}
	}
    

}
