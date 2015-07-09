package ru.hse.smartrefrigerator.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.File;
import java.io.IOException;

import ru.hse.smartrefrigerator.R;
import ru.hse.smartrefrigerator.audio.PcmAudioHelper;
import ru.hse.smartrefrigerator.audio.WavAudioFormat;

public class InputProductActivity extends Activity {
    String recordFileName;
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;
    String resultAudio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/result.wav";
    private void startRecording(){
        recordFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        recordFileName += "/audiorecordtest";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
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
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(recordFileName);
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            Log.e("RECORD", "prepare() failed");
            mPlayer.release();
            mPlayer = null;
        }
        finally {
            //stopPlaying();
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
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(InputProductActivity.this);
                AlertDialog alert = aBuilder.create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.record_dialog_layout, null);
                aBuilder.setView(dialogView);
                aBuilder.setCancelable(false);
                aBuilder.setNeutralButton("Стоп", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRecording();
                    }
                });
                aBuilder.setCancelable(false);
                aBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        stopRecording();
                    }
                });
                aBuilder.show();

                startRecording();
            }
        });

        ImageButton stopRecordVoice = (ImageButton)findViewById(R.id.bCodeScanner);
        stopRecordVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopRecording();
                final SpeechToText service = new SpeechToText();
                service.setUsernameAndPassword("2f23219b-51db-4b8a-925b-8dc0692cb2cc", "yaajYVcCmuYY");

                final File audio = new File(recordFileName);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WavAudioFormat format = WavAudioFormat.wavFormat(44100,16,2);
                        try {
                            PcmAudioHelper.convertRawToWav(format, new File(recordFileName), new File(resultAudio));
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(InputProductActivity.this,"ERROR",Toast.LENGTH_SHORT);
                        }

                        Looper.prepare();
                        SpeechResults transcript = service.recognize(new File(resultAudio), "audio/l16; rate=44100");
                        String temp = transcript.toString();
                        Toast.makeText(InputProductActivity.this,transcript.toString(),Toast.LENGTH_SHORT).show();
                        //startPlaying();
                    }
                }).start();
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
