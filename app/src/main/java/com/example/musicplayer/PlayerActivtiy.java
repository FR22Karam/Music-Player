package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class PlayerActivtiy extends AppCompatActivity {
    Button platBT,next,prev,btnff,btnfr;
    TextView txtstart,txtstop,txtsn;
    SeekBar seekBar;
    ImageView imageView;

    String sname;

    public static final String EXTRA_NAME ="song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateseekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_activtiy);

        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        btnff=findViewById(R.id.btnff);
        btnfr=findViewById(R.id.btnfr);
        platBT=findViewById(R.id.playBT);
        txtsn=findViewById(R.id.txtsn);
        txtstart=findViewById(R.id.txtstart);
        txtstop=findViewById(R.id.txtstop);
        seekBar=findViewById(R.id.seekbar);
        imageView=findViewById(R.id.imageveiw);


        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();

        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName= i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtsn.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname =mySongs.get(position).getName();
        txtsn.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateseekbar = new Thread()

        {
            @Override
            public void run() {
                int totalDuartion = mediaPlayer.getDuration();
                int current = 0;

                while (current<totalDuartion)
                {
                    try {
                        sleep(500);
                        current = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(current);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        final String[] endTime = {createime(mediaPlayer.getDuration())};
        txtstop.setText(endTime[0]);

        final Handler handler = new Handler();
        final  int delay =1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        platBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    platBT.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else
                {
                    platBT.setBackgroundResource(R.drawable.ic_pause_24);
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName();
                String endTime = createime(mediaPlayer.getDuration());
                txtstop.setText(endTime);

                txtsn.setText(sname);
                mediaPlayer.start();
                platBT.setBackgroundResource(R.drawable.ic_pause_24);
                startAnimation(imageView);
            }
        });



        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName();
                txtsn.setText(sname);
                String endTime = createime(mediaPlayer.getDuration());
                txtstop.setText(endTime);

                mediaPlayer.start();
                platBT.setBackgroundResource(R.drawable.ic_pause_24);
                startAnimation(imageView);
            }
        });

        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
    }

    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createime(int duration){

        String time="";
        int min =  duration/1000/60;
        int sec =  duration/1000%60;


       time+=min+":";
       if(sec<10)
       {
           time+="0";
       }
       time+=sec;

       return  time;
    }
}