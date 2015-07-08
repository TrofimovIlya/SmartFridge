package ru.hse.smartrefrigerator.activities;

import android.app.Activity;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.File;
import java.io.IOException;

import ru.hse.smartrefrigerator.R;

public class InputProductActivity extends Activity {
    String recordFileName;
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;

    private void startRecording(){
        recordFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        recordFileName += "/audiorecordtest.3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(recordFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("RECORD", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(recordFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("RECORD", "prepare() failed");
        }
        finally {
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_product);

        final ImageButton startRecordVoice = (ImageButton)findViewById(R.id.bVoiceRecorder);
        startRecordVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startRecording();
            }
        });

        ImageButton stopRecordVoice = (ImageButton)findViewById(R.id.bCodeScanner);
        stopRecordVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                SpeechToText service = new SpeechToText();
                service.setUsernameAndPassword("<username>", "<password>");

                File audio = new File(recordFileName);
                //SpeechResults transcript = service.recognize(audio, "audio/l16; rate=44100");
                startPlaying();




            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_product, menu);
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
}
