package ru.hse.smartrefrigerator.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.*;

import ru.hse.smartrefrigerator.R;
import ru.hse.smartrefrigerator.controllers.AudioReciever;

public class InputProductActivity extends Activity {
    String recordFileName;
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;
    AudioReciever mReciever;

    public void writeFile(short[] data, String fileName) throws IOException {
        DataOutputStream doStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        doStream.writeInt(data.length); //Save size
        for (int i = 0; i < data.length; i++) {
            doStream.writeShort(data[i]); //Save each number
        }

        doStream.flush();
        doStream.close();
    }

    private void startRecording() {
//        recordFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        recordFileName += "/audiorecordtest.wav";
        mReciever = new AudioReciever();

        mReciever.startRecording();



//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setAudioSamplingRate(44100);
//        mRecorder.setAudioEncodingBitRate(16);
//        mRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.);
//        mRecorder.setOutputFile(recordFileName);
//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e("RECORD", "prepare() failed");
//        }
//
//        mRecorder.start();
    }

    private void stopRecording() {
        mReciever.stopRecording();

//        try {
//            mRecorder.stop();
//            mRecorder.release();
//            mRecorder = null;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(recordFileName);
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            Log.e("RECORD", "prepare() failed");
        } finally {
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

        final ImageButton startRecordVoice = (ImageButton) findViewById(R.id.bVoiceRecorder);
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


        ImageButton stopRecordVoice = (ImageButton) findViewById(R.id.bCodeScanner);
        stopRecordVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopRecording();
                final SpeechToText service = new SpeechToText();
                service.setUsernameAndPassword("2f23219b-51db-4b8a-925b-8dc0692cb2cc", "yaajYVcCmuYY");

                final File audio = new File(mReciever.getFileName());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SpeechResults transcript = service.recognize(audio, "audio/l16; rate=44100");

                        System.out.println(transcript.toString());
                        //startPlaying();
                    }
                }).start();
               // startPlaying();
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
