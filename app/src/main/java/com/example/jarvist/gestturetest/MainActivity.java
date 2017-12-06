package com.example.jarvist.gestturetest;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,View.OnTouchListener{

    private final static int VOLUME = 1;
    private final static int BRIGHTNESS = 2;
    private final static float STEP_VOLUME = 1f;
    private SeekBar volumeSeekbar;
    private SeekBar brightnessSeekbar;
    private RelativeLayout rllt;
    private float start_Y;
    private float start_X;
    private boolean isFirstScroll = false;
    private float screenHeight;
    private float screenWidth;
    private int GESTURE_FLAG;
    private float touchRange;
    private int currentVolume;
    private int maxVolume;
    private AudioManager audioManager;
    private GestureDetector detector;
    private RelativeLayout volumeLayout;
    private RelativeLayout brightnessLayout;
    private TextView volumeText;
    private TextView brightnessText;
    private ImageView btPic;
    private ImageView vlPic;
    private boolean isMute = true;
    //private WebView webView;
    private VideoView videoView;
    private ImageButton btn;
    private boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    public void initViews(){
        detector = new GestureDetector(this,this);
        volumeSeekbar = findViewById(R.id.volumeSeekbar);
        brightnessSeekbar = findViewById(R.id.BrightnessSeekbar);
        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(
                "http://112.253.22.157/17/z/z/y/u/zzyuasjwufnqerzvyxgkuigrkcatxr/" +
                        "hc.yinyuetai.com/D046015255134077DDB3ACA0D7E68D45.flv"));
        MediaController controller = new MediaController(this);

        /*webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://m.douyu.com/58428");*/
        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying){
                    videoView.start();
                    btn.setImageResource(R.drawable.pause_circle);
                    isPlaying = true;
                }
                else{
                    videoView.pause();
                    btn.setImageResource(R.drawable.play_circle);
                    isPlaying = false;
                }
            }
        });
        rllt = findViewById(R.id.Parentlayout);
        //rllt.setOnTouchListener(this);
        volumeLayout = findViewById(R.id.volumeLayout);
        brightnessLayout = findViewById(R.id.brightnessLayout);
        brightnessText = findViewById(R.id.brightnessText);
        volumeText = findViewById(R.id.volumeText);
        vlPic = findViewById(R.id.volumePicture);
        btPic = findViewById(R.id.BrightnessPicture);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //Toast.makeText(MainActivity.this,maxVolume,Toast.LENGTH_SHORT).show();
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_UP){
            GESTURE_FLAG = 0;
            //松开时隐藏音量以及亮度的进度条
            volumeLayout.setVisibility(View.GONE);
            brightnessLayout.setVisibility(View.GONE);
        }

        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        isFirstScroll = true;
        screenHeight = rllt.getHeight();
        screenWidth = rllt.getWidth();
        touchRange = Math.min(screenHeight,screenWidth);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        start_X = e1.getX();
        start_Y = e1.getY();
        int y = (int)e2.getRawY();
        if(isFirstScroll){
            //亮度
            Log.d("x",String.valueOf(start_X));
            if(start_X <= 0.5*screenWidth){
                GESTURE_FLAG = BRIGHTNESS;
                //显示声音亮度
                brightnessLayout.setVisibility(View.VISIBLE);
                volumeLayout.setVisibility(View.GONE);
            }
            else{
                GESTURE_FLAG = VOLUME;
                //显示声音亮度
                brightnessLayout.setVisibility(View.GONE);
                volumeLayout.setVisibility(View.VISIBLE);
            }
        }
        if(GESTURE_FLAG == VOLUME){
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float volChange;
            if(distanceY >0)
                volChange = (distanceY * 1.1f /screenHeight) * maxVolume;
            else
                volChange = (distanceY * 0.05f /screenHeight) * maxVolume;
            int volume = (int)Math.min(Math.max(volChange  + currentVolume,0),maxVolume);
            if(volChange != 0){
                isMute = false;
                volumeUpdate(volume,isMute);
            }
        }
        else if(GESTURE_FLAG  == BRIGHTNESS){
            final double DISTANCE = 0.5;
            if(distanceY > DISTANCE)
                brightnessUpdate(3);
            else
                brightnessUpdate(-3);

        }
        isFirstScroll = false;
        return false;
    }
    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    protected void brightnessUpdate(float brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = layoutParams.screenBrightness + brightness/255.0f;
        if(layoutParams.screenBrightness > 1)
            layoutParams.screenBrightness = 1;
        else if(layoutParams.screenBrightness < 0.1)
            layoutParams.screenBrightness = (float)0.1;
        getWindow().setAttributes(layoutParams);
        brightnessSeekbar.setProgress((int)((layoutParams.screenBrightness*(double)(1000/9)-(double)(100/9))));
        brightnessText.setText((int)((layoutParams.screenBrightness*(double)(1000/9)-(double)(100/9))) + "%");

    }


    protected void volumeUpdate(int progress, boolean isMute) {
        if(isMute) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            vlPic.setImageResource(R.drawable.volume_mute);
            volumeSeekbar.setProgress(0);
            volumeText.setText(0 + "%");
            }
        else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            currentVolume = progress;
            if(currentVolume == 0)
                vlPic.setImageResource(R.drawable.volume_mute);
            else if (currentVolume > 10)
                vlPic.setImageResource(R.drawable.volume_high);
            else
                vlPic.setImageResource(R.drawable.volume_medium);
            volumeSeekbar.setProgress(progress);
            int percentage = (progress*100)/maxVolume;
            volumeText.setText(percentage + "%");
            }
    }
}

