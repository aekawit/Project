package com.example.someapi;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;




import android.app.Activity;
import java.util.ArrayList;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View.OnClickListener;

public class MainActivity extends ActionBarActivity {

	
	

	protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    private TextView edText;
	
    
    final static int INTENT_CHECK_TTS = 0;
    TextToSpeech tts;
    EditText et;
    Button btn;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        
        
        
        
        edText = (TextView) findViewById(R.id.editText);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    edText.setText("");
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

        et = (EditText)findViewById(R.id.editText);
        
        btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String str = et.getText().toString();
                tts.speak(str, TextToSpeech.QUEUE_ADD, null);
            }
        });
        
        
        
        
        
}
    
    
    
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
     

        
        
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            
        	
        	
        	super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edText.setText(text.get(0));
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
        
        
        
        
    }

