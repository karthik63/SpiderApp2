package com.example.samharris.spiderapp2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Thread picUpdateThread;
    Thread cdTimerThread;
    Thread playThread;
    ImageView musicPic;
    MediaPlayer mPlayer;
    ImageButton btNext;
    ImageButton btPlay;
    ImageButton btStop;
    Spinner songs;
    TextView tvCountdown;
    ArrayAdapter<CharSequence> songList;
    boolean ifCDrunning;
    private int currentState = 0;
    Handler hPictureUpdate = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(currentState)
            {
                case 0:
                    musicPic.setImageResource(R.drawable.smooth_criminal);
                    break;

                case 1:
                    musicPic.setImageResource(R.drawable.under_pressure);
                    break;

                case 2:
                    musicPic.setImageResource(R.drawable.roboto);
                    break;

                case 3:
                    musicPic.setImageResource(R.drawable.br);
            }
        }
    };
    private int countDown = 3;
    Handler hCountUpdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvCountdown=(TextView)findViewById(R.id.tvTimer);

            if(countDown<=0)
                tvCountdown.setText(":-)");

            else
                tvCountdown.setText("Loading... "+String.valueOf(countDown));
        }
    };

    private void playMusic()
    {
        Runnable play = new Runnable() {
            @Override
            public void run() {

                if (ifCDrunning)
                {
                    synchronized (this) {
                        try {
                            wait(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(mPlayer.isPlaying())
                    mPlayer.stop();

                switch(currentState)
                {
                    case 0:
                        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.smooth_criminal);
                        mPlayer.start();
                        break;

                    case 1:
                        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.under_pressure);
                        mPlayer.start();
                        break;

                    case 2:
                        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.mr_roboto);
                        mPlayer.start();
                        break;

                    case 3:
                        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.bohemian_rhapsody);
                        mPlayer.start();
                }
            }
        };

        playThread=new Thread(play);

        playThread.start();
    }

    private void updatePicture()
    {
        Runnable pictureUpdate = new Runnable() {
            @Override
            public void run() {

                if (ifCDrunning)
                {
                    synchronized (this) {
                        try {
                            wait(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

                hPictureUpdate.sendEmptyMessage(0);
            }
        };

        picUpdateThread=new Thread(pictureUpdate);

        picUpdateThread.start();
    }

    private void runCDTimer(){

        Runnable rRunCDTimer = new Runnable() {
            @Override
            public void run() {

                countDown=3;
                hCountUpdate.sendEmptyMessage(0);

                for (int i = 3; i >= 0; i--) {

                    hCountUpdate.sendEmptyMessage(0);

                    synchronized (this) {
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    countDown--;

                    hCountUpdate.sendEmptyMessage(0);

                }
            }
        };

        cdTimerThread=new Thread(rRunCDTimer);

        cdTimerThread.start();

    }

    private void changeSlide() {

        runCDTimer();

        ifCDrunning = true;

        updatePicture();

        playMusic();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCountdown = (TextView)findViewById(R.id.tvTimer);
        musicPic = (ImageView) findViewById(R.id.musicPic);
        musicPic.setImageResource(R.drawable.smooth_criminal);
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.smooth_criminal);
        songs = (Spinner) findViewById(R.id.spinner);
        songList = ArrayAdapter.createFromResource(this, R.array.songsArray, android.R.layout.simple_spinner_item);
        songList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        songs.setAdapter(songList);
        songs.setSelection(4);
        btNext = (ImageButton)findViewById(R.id.btNext);
        btPlay = (ImageButton)findViewById(R.id.btPlay);
        btStop = (ImageButton) findViewById(R.id.btStop);

        songs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                int selection = songs.getSelectedItemPosition();

                switch (selection) {
                    case 0:
                        currentState = 0;
                        break;

                    case 1:
                        currentState = 1;
                        break;

                    case 2:
                        currentState = 2;
                        break;

                    case 3:
                        currentState = 3;
                }

                if (songs.getSelectedItemPosition() != 4)
                    changeSlide();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do Nothing
            }


        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ifCDrunning = false;
                playMusic();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentState= (currentState+1)%4;

                changeSlide();

                songs.setSelection(4);

            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPlayer.isPlaying())
                    mPlayer.stop();
            }
        });
    }
}