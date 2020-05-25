package com.edu.avas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    public VideoView courseTopicVideo;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        courseTopicVideo = findViewById(R.id.courseTopicVideo);
        courseTopicVideo.setVideoURI(Uri.parse(url));
        courseTopicVideo.start();
        mediaController = new MediaController(this);
        courseTopicVideo.setMediaController(mediaController);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mediaController.setAnchorView(courseTopicVideo);
    }
}