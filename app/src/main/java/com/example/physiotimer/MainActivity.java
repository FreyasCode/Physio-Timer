package com.example.physiotimer;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // animation

    public int loop = 2; // number of rounds/physio movements
    public int timesElap = 10; // length per round
    Timer t;
    int count = -1;
    int breakTime = 5;
    TextView txtShowTime;
    TimerTask timerTask;
    TextView txtText;
    Thread th;
    boolean status = true; // t:physio time f:break time
    boolean restart = false; // f: ongoing cycle t: restart ** attempting to fix **

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtShowTime = (TextView) findViewById(R.id.txtShowTime);
        // TimerTask timerTask;
        txtText = (TextView) findViewById(R.id.txtText);

        final MediaPlayer start = MediaPlayer.create(this, R.raw.start); // starting cycle sound
        // final MediaPlayer restartSound = MediaPlayer.create(this, R.raw.restart); // sound for starting a new cycle
        final MediaPlayer resume = MediaPlayer.create(this, R.raw.resume); // end of break time sound
        final MediaPlayer stop = MediaPlayer.create(this, R.raw.stop); // end of one physio round sound


        // timer thread
        th = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {


                        Thread.sleep(1000); // pause execution for 1 second

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loop > 0) { // check if preset number of rounds is reached

                                    if (status == true) { // time for some movements!
                                        // resume.start();
                                        txtText.setText("Action");
                                        count++;

                                        if (count == loop && loop == 0) {
                                            txtShowTime.setText("");

                                        } else if (count == timesElap+1) { // check if length per round is reached
                                            loop--;
                                            count = 0;
                                            if (loop > 0) { // if # > 0 rounds remaining
                                                stop.start();
                                                txtText.setText("Break Time");
                                                status = false;
                                            }
                                        }
                                        txtShowTime.setText(String.valueOf(count));

                                    } else { // break time!
                                        if (count <= breakTime) { // countdown
                                            count++;
                                            txtShowTime.setText(String.valueOf(count));
                                        }

                                        if (count == breakTime+1) { // break time is over
                                            count = 0;
                                            txtShowTime.setText(String.valueOf(count));
                                            status = true;
                                            txtText.setText("Ready...");
                                            resume.start();
                                        }
                                    }
                                } else { // cycle finished
                                    txtShowTime.setText("√");
                                    txtText.setText("Today's Physio");
                                    restart = false;
                                    // th.stop();
                                }

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };


        Button btnStart = (Button) this.findViewById(R.id.btnStart);
        // create();
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (restart == false) { // start new round
                    restart = true;
                    start.start();
                    th.start();

                } else { // pause
                    restart = false;
                }
                //}
                // setTime();
            }
        });
    }
}