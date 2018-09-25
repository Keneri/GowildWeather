package com.example.keneri.gowildweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static TextView location;
    static TextView description;

    //TODO: Creating the text to speech and speech recognizer objects
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO:Trying to implement TextView into the application but unfortunately could not get it to work
        location = (TextView) findViewById(R.id.textLocation);
        description = (TextView) findViewById(R.id.textDescription);

        //TODO:Getting the location of the user
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        //TODO: Getting the weather from the OpenWeatherMap API
        DownloadTask task = new DownloadTask();
        task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&appid=955f0e6f4528a43be540e53a6e386242");

        //TODO: This is the button that the user will press to input the speech recognition
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                speechRecognizer.startListening(intent);
            }
        });

        //TODO: This is to initialize text to speech and speech recognizer for the speech input
        initTextToSpeech();
        initSpeechRecognizer();
    }

    //TODO:Initializes the speech recognizer. Checks if speech recognition is available on the device
    private void initSpeechRecognizer(){
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                //TODO: This method is used when the speech recognition is complete & the results are in an array

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    //TODO: The method then chooses the most confident answer it has and chooses the first/best result
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }


    //TODO: This method will tell the app what to output whenever the user gives an input, in this case, the weather of the area
    private void processResult(String speech){
        speech = speech.toLowerCase();

        if (speech.contains("weather")) {
            speak("The weather today at " + location + " is " + description);
        }
    }

    //TODO: Creating the initializing of the text to speech. This also checks for text to speech in the device
    private void initTextToSpeech(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "Sorry, your device does not support voice inputs!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    textToSpeech.setLanguage(Locale.getDefault());
                    speak("Hello, I am ready!");
                }

            }
        });
    }

    //TODO: Creating the speak method for the app to talk
    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else{
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: Closes the text to speech
    @Override
    protected void onPause(){
        super.onPause();
        textToSpeech.shutdown();
    }
}
