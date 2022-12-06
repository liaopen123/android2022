package com.lph.livephotoview.jz_real;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lph.livephotoview.jz.JZMediaInterface;
import com.lph.livephotoview.jz.JZMediaSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZTextureView;
import cn.jzvd.JZUtils;

/**
 * Created by Nathen on 16/7/30.
 */
public abstract class Jzvd extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    public static final String TAG = "JZVD";
    public static final int SCREEN_NORMAL = 0;
    public static final int SCREEN_FULLSCREEN = 1;
    public static final int SCREEN_TINY = 2;
    public static final int STATE_IDLE = -1;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARING_CHANGE_URL = 2;
    public static final int STATE_PREPARING_PLAYING = 3;
    public static final int STATE_PREPARED = 4;
    public static final int STATE_PLAYING = 5;
    public static final int STATE_PAUSE = 6;
    public static final int STATE_AUTO_COMPLETE = 7;
    public static final int STATE_ERROR = 8;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER = 0;//DEFAULT
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 1;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 2;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 3;
    public static final int THRESHOLD = 80;
    public static Jzvd CURRENT_JZVD;
    public static LinkedList<ViewGroup> CONTAINER_LIST = new LinkedList<>();
    public static boolean TOOL_BAR_EXIST = true;
    public static int FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    public static int NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static boolean SAVE_PROGRESS = true;
    public static boolean WIFI_TIP_DIALOG_SHOWED = false;
    public static int VIDEO_IMAGE_DISPLAY_TYPE = 0;
    public static long lastAutoFullscreenTime = 0;
    public static int ON_PLAY_PAUSE_TMP_STATE = 0;//这个考虑不放到库里，去自定义
    public static int backUpBufferState = -1;
    public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseAllVideos();
                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    try {
                        Jzvd player = CURRENT_JZVD;
                        if (player != null && player.state == Jzvd.STATE_PLAYING) {
                            player.startButton.performClick();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };
    public int state = -1;
    public int screen = -1;
    public JZDataSource jzDataSource;
    public int widthRatio = 0;
    public int heightRatio = 0;
    public Class mediaInterfaceClass;
    public JZMediaInterface mediaInterface;
    public int positionInList = -1;//很想干掉它
    public int videoRotation = 0;
    public int seekToManulPosition = -1;
    public long seekToInAdvance = 0;

    public ImageView startButton;
    public ViewGroup textureViewContainer;
    public JZTextureView textureView;
    public boolean preloading = false;
    protected long gobakFullscreenTime = 0;//这个应该重写一下，刷新列表，新增列表的刷新，不打断播放，应该是个flag
    protected long gotoFullscreenTime = 0;
    protected AudioManager mAudioManager;
    protected Context jzvdContext;
    /**
     * 如果不在列表中可以不加block
     */
    protected ViewGroup.LayoutParams blockLayoutParams;
    protected int blockIndex;
    protected int blockWidth;
    protected int blockHeight;

    public Jzvd(Context context) {
        super(context);
        init(context);
    }

    public Jzvd(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 增加准备状态逻辑
     * lph当onResume的时候 继续播放
     */
    public static void goOnPlayOnResume() {
        if (CURRENT_JZVD != null) {
            if (CURRENT_JZVD.state == Jzvd.STATE_PAUSE) {
                if (ON_PLAY_PAUSE_TMP_STATE == STATE_PAUSE) {
                    CURRENT_JZVD.onStatePause();
                    CURRENT_JZVD.mediaInterface.pause();
                } else {
                    CURRENT_JZVD.onStatePlaying();
                    CURRENT_JZVD.mediaInterface.start();
                }
                ON_PLAY_PAUSE_TMP_STATE = 0;
            } else if (CURRENT_JZVD.state == Jzvd.STATE_PREPARING) {
                //准备状态暂停后的
                CURRENT_JZVD.startVideo();
            }
            if (CURRENT_JZVD.screen == Jzvd.SCREEN_FULLSCREEN) {
                JZUtils.hideStatusBar(CURRENT_JZVD.jzvdContext);
                JZUtils.hideSystemUI(CURRENT_JZVD.jzvdContext);
            }
        }
    }

    /**
     * 增加准备状态逻辑
     * lph 见名知意
     */
    public static void goOnPlayOnPause() {
        if (CURRENT_JZVD != null) {
            if (CURRENT_JZVD.state == Jzvd.STATE_AUTO_COMPLETE ||
                    CURRENT_JZVD.state == Jzvd.STATE_NORMAL ||
                    CURRENT_JZVD.state == Jzvd.STATE_ERROR) {
                Jzvd.releaseAllVideos();
            } else if (CURRENT_JZVD.state == Jzvd.STATE_PREPARING) {
                //准备状态暂停的逻辑
                Jzvd.setCurrentJzvd(CURRENT_JZVD);
                CURRENT_JZVD.state = STATE_PREPARING;
            } else {
                ON_PLAY_PAUSE_TMP_STATE = CURRENT_JZVD.state;
                CURRENT_JZVD.onStatePause();
                CURRENT_JZVD.mediaInterface.pause();
            }
        }
    }


    public static void releaseAllVideos() {
        Log.d(TAG, "releaseAllVideos");
        if (CURRENT_JZVD != null) {
            CURRENT_JZVD.reset();
            CURRENT_JZVD = null;
        }
    }



    public static void setCurrentJzvd(Jzvd jzvd) {
        if (CURRENT_JZVD != null) CURRENT_JZVD.reset();
        CURRENT_JZVD = jzvd;
    }


    public abstract int getLayoutId();

    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        jzvdContext = context;
        startButton = findViewById(cn.jzvd.R.id.start);
        textureViewContainer = findViewById(cn.jzvd.R.id.surface_container);

        if (startButton == null) {
            startButton = new ImageView(context);
        }
        if (textureViewContainer == null) {
            textureViewContainer = new FrameLayout(context);
        }

        startButton.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);


        state = STATE_IDLE;
    }

    public void setUp(String url, String title) {
        setUp(new JZDataSource(url, title), SCREEN_NORMAL);
    }

    public void setUp(String url, String title, int screen) {
        setUp(new JZDataSource(url, title), screen);
    }

    public void setUp(JZDataSource jzDataSource, int screen) {
        setUp(jzDataSource, screen, JZMediaSystem.class);
    }

    public void setUp(String url, String title, int screen, Class mediaInterfaceClass) {
        setUp(new JZDataSource(url, title), screen, mediaInterfaceClass);
    }

    public void setUp(JZDataSource jzDataSource, int screen, Class mediaInterfaceClass) {


        this.jzDataSource = jzDataSource;
        this.screen = screen;
        onStateNormal();
        this.mediaInterfaceClass = mediaInterfaceClass;
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == cn.jzvd.R.id.start) {
            clickStart();
        } else if (i == cn.jzvd.R.id.fullscreen) {
            clickFullscreen();
        }
    }

    protected void clickFullscreen() {
        Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
        if (state == STATE_AUTO_COMPLETE) return;
        if (screen == SCREEN_FULLSCREEN) {
            //quit fullscreen
        } else {
            Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
        }
    }

    /**
     * lph:点按钮 手动播放视频
     */
    protected void clickStart() {
        Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
        if (jzDataSource == null || jzDataSource.urlsMap.isEmpty() || jzDataSource.getCurrentUrl() == null) {
            Toast.makeText(getContext(), getResources().getString(cn.jzvd.R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (state == STATE_NORMAL) {
            if (!jzDataSource.getCurrentUrl().toString().startsWith("file") && !
                    jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                    !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {//这个可以放到std中
                showWifiDialog();
                return;
            }
            startVideo();
        } else if (state == STATE_PLAYING) {
            Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
            mediaInterface.pause();
            onStatePause();
        } else if (state == STATE_PAUSE) {
            mediaInterface.start();
            onStatePlaying();
        } else if (state == STATE_AUTO_COMPLETE) {
            startVideo();
        }
    }


    public void onStateNormal() {
        Log.i(TAG, "onStateNormal " + " [" + this.hashCode() + "] ");
        state = STATE_NORMAL;
        if (mediaInterface != null) mediaInterface.release();
    }

    public void onStatePreparing() {
        Log.i(TAG, "onStatePreparing " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARING;
    }

    public void onStatePreparingPlaying() {
        Log.i(TAG, "onStatePreparingPlaying " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARING_PLAYING;
    }

    public void onStatePreparingChangeUrl() {
        Log.i(TAG, "onStatePreparingChangeUrl " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARING_CHANGE_URL;

        releaseAllVideos();
        startVideo();

//        mediaInterface.prepare();
    }


    public void onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARED;
        if (!preloading) {
            mediaInterface.start();//这里原来是非县城
            preloading = false;
        }
        if (jzDataSource.getCurrentUrl().toString().toLowerCase().contains("mp3") ||
                jzDataSource.getCurrentUrl().toString().toLowerCase().contains("wma") ||
                jzDataSource.getCurrentUrl().toString().toLowerCase().contains("aac") ||
                jzDataSource.getCurrentUrl().toString().toLowerCase().contains("m4a") ||
                jzDataSource.getCurrentUrl().toString().toLowerCase().contains("wav")) {
            onStatePlaying();
        }
    }

    public void startPreloading() {
        preloading = true;
        startVideo();
    }

    /**
     * 如果STATE_PREPARED就播放，如果没准备完成就走正常的播放函数startVideo();
     */
    public void startVideoAfterPreloading() {
        if (state == STATE_PREPARED) {
            mediaInterface.start();
        } else {
            preloading = false;
            startVideo();
        }
    }

    public void onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ");
        if (state == STATE_PREPARED) {//如果是准备完成视频后第一次播放，先判断是否需要跳转进度。
//            if (seekToInAdvance != 0) {
//                mediaInterface.seekTo(seekToInAdvance);
//                seekToInAdvance = 0;
//            } else {
//                long position = JZUtils.getSavedProgress(getContext(), jzDataSource.getCurrentUrl());
//                if (position != 0) {
//                    mediaInterface.seekTo(position);//这里为什么区分开呢，第一次的播放和resume播放是不一样的。 这里怎么区分是一个问题。然后
//                }
//            }
            mediaInterface.seekTo(0);
        }
        state = STATE_PLAYING;
    }

    public void onStatePause() {
        Log.i(TAG, "onStatePause " + " [" + this.hashCode() + "] ");
        state = STATE_PAUSE;
    }

    public void onStateError() {
        Log.i(TAG, "onStateError " + " [" + this.hashCode() + "] ");
        state = STATE_ERROR;
    }

    public void onStateAutoComplete() {
        Log.i(TAG, "onStateAutoComplete " + " [" + this.hashCode() + "] ");
        state = STATE_AUTO_COMPLETE;
    }







    /**
     * 多数表现为中断当前播放
     */
    public void reset() {
        Log.i(TAG, "reset " + " [" + this.hashCode() + "] ");
        if (state == STATE_PLAYING || state == STATE_PAUSE) {
            long position = getCurrentPositionWhenPlaying();
            JZUtils.saveProgress(getContext(), jzDataSource.getCurrentUrl(), position);
        }
        dismissBrightnessDialog();
        dismissProgressDialog();
        dismissVolumeDialog();
        onStateNormal();
        textureViewContainer.removeAllViews();

        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        JZUtils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mediaInterface != null) mediaInterface.release();
    }


    public void setScreen(int screen) {//特殊的个别的进入全屏的按钮在这里设置  只有setup的时候能用上
        switch (screen) {
            case SCREEN_NORMAL:
                setScreenNormal();
                break;
            case SCREEN_FULLSCREEN:
                setScreenFullscreen();
                break;
            case SCREEN_TINY:
                setScreenTiny();
                break;
        }
    }

    public void startVideo() {
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        setCurrentJzvd(this);
        try {
            Constructor<JZMediaInterface> constructor = mediaInterfaceClass.getConstructor(Jzvd.class);
            this.mediaInterface = constructor.newInstance(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        addTextureView();
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        onStatePreparing();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (screen == SCREEN_FULLSCREEN || screen == SCREEN_TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        if (textureView != null) textureViewContainer.removeView(textureView);
        textureView = new JZTextureView(getContext().getApplicationContext());
        textureView.setSurfaceTextureListener(mediaInterface);

        LayoutParams layoutParams =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(textureView, layoutParams);
    }



    public void onVideoSizeChanged(int width, int height) {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (textureView != null) {
            if (videoRotation != 0) {
                textureView.setRotation(videoRotation);
            }
            textureView.setVideoSize(width, height);
        }
    }



    public void onProgress(int progress, long position, long duration) {
    }



    public void resetProgressAndTime() {
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (state == STATE_PLAYING || state == STATE_PAUSE || state == STATE_PREPARING_PLAYING) {
            try {
                position = mediaInterface.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    public long getDuration() {
        long duration = 0;
        try {
            duration = mediaInterface.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (state != STATE_PLAYING &&
                state != STATE_PAUSE) return;
        long time = seekBar.getProgress() * getDuration() / 100;
        seekToManulPosition = seekBar.getProgress();
        mediaInterface.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            long duration = getDuration();
        }
    }

    public void cloneAJzvd(ViewGroup vg) {
        try {
            Constructor<Jzvd> constructor = (Constructor<Jzvd>) Jzvd.this.getClass().getConstructor(Context.class);
            Jzvd jzvd = constructor.newInstance(getContext());
            jzvd.setId(getId());
            jzvd.setMinimumWidth(blockWidth);
            jzvd.setMinimumHeight(blockHeight);
            vg.addView(jzvd, blockIndex, blockLayoutParams);
            jzvd.setUp(jzDataSource.cloneMe(), SCREEN_NORMAL, mediaInterfaceClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public void gotoNormalScreen() {//goback本质上是goto
        gobakFullscreenTime = System.currentTimeMillis();//退出全屏
        ViewGroup vg = (ViewGroup) (JZUtils.scanForActivity(jzvdContext)).getWindow().getDecorView();
        vg.removeView(this);
//        CONTAINER_LIST.getLast().removeAllViews();
        CONTAINER_LIST.getLast().removeViewAt(blockIndex);//remove block
        CONTAINER_LIST.getLast().addView(this, blockIndex, blockLayoutParams);
        CONTAINER_LIST.pop();

        setScreenNormal();//这块可以放到jzvd中
        JZUtils.showStatusBar(jzvdContext);
        JZUtils.setRequestedOrientation(jzvdContext, NORMAL_ORIENTATION);
        JZUtils.showSystemUI(jzvdContext);
    }

    public void setScreenNormal() {//TODO 这块不对呀，还需要改进，设置flag之后要设置ui，不设置ui这么写没意义呀
        screen = SCREEN_NORMAL;
    }

    public void setScreenFullscreen() {
        screen = SCREEN_FULLSCREEN;
    }

    public void setScreenTiny() {
        screen = SCREEN_TINY;
    }

    //    //重力感应的时候调用的函数，、、这里有重力感应的参数，暂时不能删除


    public void onSeekComplete() {

    }

    public void showWifiDialog() {
    }

    public void showProgressDialog(float deltaX,
                                   String seekTime, long seekTimePosition,
                                   String totalTime, long totalTimeDuration) {
    }

    public void dismissProgressDialog() {

    }

    public void showVolumeDialog(float deltaY, int volumePercent) {

    }

    public void dismissVolumeDialog() {

    }

    public void showBrightnessDialog(int brightnessPercent) {

    }

    public void dismissBrightnessDialog() {

    }

    public Context getApplicationContext() {//这个函数必要吗
        Context context = getContext();
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                return applicationContext;
            }
        }
        return context;
    }

    public void onCompletion() {
        
    }

    public void setBufferProgress(int percent) {
    }

    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError();
            mediaInterface.release();
        }
    }

    public void onInfo(int what, int extra) {
        Log.d(TAG, "onInfo what - " + what + " extra - " + extra);
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
            if (state == cn.jzvd.Jzvd.STATE_PREPARED
                    || state == cn.jzvd.Jzvd.STATE_PREPARING_CHANGE_URL
                    || state == cn.jzvd.Jzvd.STATE_PREPARING_PLAYING) {
                onStatePlaying();//开始渲染图像，真正进入playing状态
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.d(TAG, "MEDIA_INFO_BUFFERING_START");
            backUpBufferState = state;
            setState(STATE_PREPARING_PLAYING);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
            if (backUpBufferState != -1) {
                setState(backUpBufferState);
                backUpBufferState = -1;
            }
        }
    }

    public void setState(int state) {
        switch (state) {
            case STATE_NORMAL:
                onStateNormal();
                break;
            case STATE_PREPARING:
                onStatePreparing();
                break;
            case STATE_PREPARING_PLAYING:
                onStatePreparingPlaying();
                break;
            case STATE_PREPARING_CHANGE_URL:
                onStatePreparingChangeUrl();
                break;
            case STATE_PLAYING:
                onStatePlaying();
                break;
            case STATE_PAUSE:
                onStatePause();
                break;
            case STATE_ERROR:
                onStateError();
                break;
            case STATE_AUTO_COMPLETE:
                onStateAutoComplete();
                break;
        }
    }

}
