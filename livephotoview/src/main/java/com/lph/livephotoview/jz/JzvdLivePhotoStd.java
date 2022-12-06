package com.lph.livephotoview.jz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lph.livephotoview.R;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZUtils;
import cn.jzvd.Jzvd;


/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JzvdLivePhotoStd extends HhzJzvd {

    public static long LAST_GET_BATTERYLEVEL_TIME = 0;
    public static int LAST_GET_BATTERYLEVEL_PERCENT = 70;
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;
    public VisibleListener visibleListener;

    public ProgressBar  loadingProgressBar;
    public PopupWindow clarityPopWindow;
    public BroadcastReceiver battertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int percent = level * 100 / scale;
                LAST_GET_BATTERYLEVEL_PERCENT = percent;
                setBatteryLevel();
                try {
                    jzvdContext.unregisterReceiver(battertReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    protected DismissControlViewTimerTask mDismissControlViewTimerTask;
    protected Dialog mProgressDialog;
    protected TextView mDialogSeekTime;
    protected TextView mDialogTotalTime;
    protected ImageView mDialogIcon;
    protected Dialog mVolumeDialog;
    protected ProgressBar mDialogVolumeProgressBar;
    protected TextView mDialogVolumeTextView;
    protected ImageView mDialogVolumeImageView;
    protected Dialog mBrightnessDialog;
    protected ProgressBar mDialogBrightnessProgressBar;
    protected TextView mDialogBrightnessTextView;
    protected boolean mIsWifi;
    public BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean isWifi = JZUtils.isWifiConnected(context);
                if (mIsWifi == isWifi) return;
                mIsWifi = isWifi;
                if (!mIsWifi && !WIFI_TIP_DIALOG_SHOWED && state == STATE_PLAYING) {
                    showWifiDialog();
                }
            }
        }
    };
    //doublClick 这两个全局变量只在ontouch中使用，就近放置便于阅读
    protected long lastClickTime = 0;
    protected long doubleTime = 200;
    protected ArrayDeque<Runnable> delayTask = new ArrayDeque<>();

    public JzvdLivePhotoStd(Context context) {
        super(context);
    }

    public JzvdLivePhotoStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        loadingProgressBar = findViewById(R.id.loading);

        if (loadingProgressBar == null) {
            loadingProgressBar = new ProgressBar(context);
        }

    }

    public void setUp(JZDataSource jzDataSource, int screen, Class mediaInterfaceClass) {
        if ((System.currentTimeMillis() - gobakFullscreenTime) < 200) {
            return;
        }

        if ((System.currentTimeMillis() - gotoFullscreenTime) < 200) {
            return;
        }


        super.setUp(jzDataSource, screen, mediaInterfaceClass);
        setScreen(screen);
    }

    @Override
    public void changeUrl(JZDataSource jzDataSource, long seekToInAdvance) {
        super.changeUrl(jzDataSource, seekToInAdvance);
    }

    public void changeStartButtonSize(int size) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.hhz_jz_layout_std;
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        changeUiToNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        changeUiToPreparing();
    }

    public void onStatePreparingPlaying() {
        super.onStatePreparingPlaying();
        changeUIToPreparingPlaying();
    }

    public void onStatePreparingChangeUrl() {
        super.onStatePreparingChangeUrl();
        changeUIToPreparingChangeUrl();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        changeUiToPlayingClear();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        changeUiToPauseShow();
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        if (visibleListener!=null) {
            visibleListener.visible1(true);
        }
        changeUiToComplete();
        cancelDismissControlViewTimer();
    }

    @Override
    public void startVideo() {
        super.startVideo();
        registerWifiListener(getApplicationContext());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    if (mChangePosition) {
                        long duration = getDuration();
                        int progress = (int) (mSeekTimePosition * 100 / (duration == 0 ? 1 : duration));
                    }

                    //加上延时是为了判断点击是否是双击之一，双击不执行这个逻辑
                    Runnable task = () -> {
                        if (!mChangePosition && !mChangeVolume) {
                            onClickUiToggle();
                        }
                    };
                    v.postDelayed(task, doubleTime + 20);
                    delayTask.add(task);
                    while (delayTask.size() > 2) {
                        delayTask.pollFirst();
                    }

                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastClickTime < doubleTime) {
                        for (Runnable taskItem : delayTask) {
                            v.removeCallbacks(taskItem);
                        }
                        if (state == STATE_PLAYING || state == STATE_PAUSE) {
                            Log.d(TAG, "doublClick [" + this.hashCode() + "] ");
                        }
                    }
                    lastClickTime = currentTimeMillis;
                    break;
            }
        } else if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
            }
        }
        return super.onTouch(v, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.poster) {
            clickPoster();
        } else if (i == R.id.surface_container) {
            clickSurfaceContainer();
            if (clarityPopWindow != null) {
                clarityPopWindow.dismiss();
            }
        } else if (i == R.id.back) {
            clickBack();
        } else if (i == R.id.back_tiny) {
            clickBackTiny();
        } else if (i == R.id.clarity) {
            clickClarity();
        }
    }
    //重播！！！！！！
    protected void clickRetryBtn() {
        if (jzDataSource.urlsMap.isEmpty() || jzDataSource.getCurrentUrl() == null) {
            Toast.makeText(jzvdContext, getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!jzDataSource.getCurrentUrl().toString().startsWith("file") && !
                jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                !JZUtils.isWifiConnected(jzvdContext) && !WIFI_TIP_DIALOG_SHOWED) {
            showWifiDialog();
            return;
        }
        seekToInAdvance = mCurrentPosition;
        startVideo();
    }

    protected void clickClarity() {
        onCLickUiToggleToClear();

        LayoutInflater inflater = (LayoutInflater) jzvdContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.jz_layout_clarity, null);

        OnClickListener mQualityListener = v1 -> {
            int index = (int) v1.getTag();

//                this.seekToInAdvance = getCurrentPositionWhenPlaying();
            jzDataSource.currentUrlIndex = index;
//                onStatePreparingChangeUrl();

            changeUrl(jzDataSource, getCurrentPositionWhenPlaying());

            for (int j = 0; j < layout.getChildCount(); j++) {//设置点击之后的颜色
                if (j == jzDataSource.currentUrlIndex) {
                    ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#fff85959"));
                } else {
                    ((TextView) layout.getChildAt(j)).setTextColor(Color.parseColor("#ffffff"));
                }
            }
            if (clarityPopWindow != null) {
                clarityPopWindow.dismiss();
            }
        };

        for (int j = 0; j < jzDataSource.urlsMap.size(); j++) {
            String key = jzDataSource.getKeyFromDataSource(j);
            TextView clarityItem = (TextView) View.inflate(jzvdContext, R.layout.jz_layout_clarity_item, null);
            clarityItem.setText(key);
            clarityItem.setTag(j);
            layout.addView(clarityItem, j);
            clarityItem.setOnClickListener(mQualityListener);
            if (j == jzDataSource.currentUrlIndex) {
                clarityItem.setTextColor(Color.parseColor("#fff85959"));
            }
        }

        clarityPopWindow = new PopupWindow(layout, JZUtils.dip2px(jzvdContext, 240), LayoutParams.MATCH_PARENT, true);
        clarityPopWindow.setContentView(layout);
        clarityPopWindow.setAnimationStyle(R.style.pop_animation);
        clarityPopWindow.showAtLocation(textureViewContainer, Gravity.END, 0, 0);
//            int offsetX = clarity.getMeasuredWidth() / 3;
//            int offsetY = clarity.getMeasuredHeight() / 3;
//            clarityPopWindow.update(clarity, -offsetX, -offsetY, Math.round(layout.getMeasuredWidth() * 2), layout.getMeasuredHeight());
    }

    protected void clickBackTiny() {
        clearFloatScreen();
    }

    protected void clickBack() {
        backPress();
    }

    protected void clickSurfaceContainer() {
        startDismissControlViewTimer();
    }

    protected void clickPoster() {
        if (jzDataSource == null || jzDataSource.urlsMap.isEmpty() || jzDataSource.getCurrentUrl() == null) {
            Toast.makeText(jzvdContext, getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (state == STATE_NORMAL) {
            if (!jzDataSource.getCurrentUrl().toString().startsWith("file") &&
                    !jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                    !JZUtils.isWifiConnected(jzvdContext) && !WIFI_TIP_DIALOG_SHOWED) {
                showWifiDialog();
                return;
            }
            startVideo();
        } else if (state == STATE_AUTO_COMPLETE) {
            onClickUiToggle();
        }
    }

    @Override
    public void setScreenNormal() {
        super.setScreenNormal();
        changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_normal));
    }

    @Override
    public void setScreenFullscreen() {
        super.setScreenFullscreen();
        //进入全屏之后要保证原来的播放状态和ui状态不变，改变个别的ui

        changeStartButtonSize((int) getResources().getDimension(R.dimen.jz_start_button_w_h_fullscreen));
        setSystemTimeAndBattery();
    }

    @Override
    public void setScreenTiny() {
        super.setScreenTiny();
        setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
    }

    @Override
    public void showWifiDialog() {
        super.showWifiDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(jzvdContext);
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), (dialog, which) -> {
            dialog.dismiss();
            WIFI_TIP_DIALOG_SHOWED = true;
            if (state == STATE_PAUSE) {
            } else {
                startVideo();
            }

        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), (dialog, which) -> {
            dialog.dismiss();
            releaseAllVideos();
            clearFloatScreen();
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                releaseAllVideos();
                clearFloatScreen();
            }
        });

        builder.create().show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    public void onClickUiToggle() {//这是事件

        if (state == STATE_PREPARING) {
            changeUiToPreparing();

        } else if (state == STATE_PLAYING) {

        } else if (state == STATE_PAUSE) {

        }
    }

    public void setSystemTimeAndBattery() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        if ((System.currentTimeMillis() - LAST_GET_BATTERYLEVEL_TIME) > 30000) {
            LAST_GET_BATTERYLEVEL_TIME = System.currentTimeMillis();
            jzvdContext.registerReceiver(
                    battertReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );
        } else {
            setBatteryLevel();
        }
    }

    public void setBatteryLevel() {

    }

    //** 和onClickUiToggle重复，要干掉
    public void onCLickUiToggleToClear() {
        if (state == STATE_PREPARING) {

        } else if (state == STATE_PLAYING) {

        } else if (state == STATE_PAUSE) {

        } else if (state == STATE_AUTO_COMPLETE) {

        }
    }

    @Override
    public void onProgress(int progress, long position, long duration) {
        super.onProgress(progress, position, duration);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        super.setBufferProgress(bufferProgress);
    }

    @Override
    public void resetProgressAndTime() {
        super.resetProgressAndTime();
    }

    public void changeUiToNormal() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }
    }

    public void changeUiToPreparing() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }
    }

    public void changeUIToPreparingPlaying() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }
    }

    public void changeUIToPreparingChangeUrl() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }
    }

    public void changeUiToPlayingShow() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }

    }

    public void changeUiToPlayingClear() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_TINY:
                break;
        }

    }

    public void changeUiToPauseShow() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }
    }

    public void changeUiToPauseClear() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_TINY:
                break;
        }

    }

    public void changeUiToComplete() {
        switch (screen) {
            case SCREEN_NORMAL:
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }

    }

    public void changeUiToError() {
        switch (screen) {
            case SCREEN_NORMAL:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_TINY:
                break;
        }

    }

    public void setAllControlsVisiblity(int topCon, int bottomCon, int startBtn, int loadingPro,
                                        int posterImg, int bottomPro, int retryLayout) {
        loadingProgressBar.setVisibility(loadingPro);
        if (loadingPro==View.INVISIBLE&&visibleListener!=null) {
            visibleListener.visible1(false);
        }

    }
    public interface  VisibleListener{
        public void visible1(boolean hide);
    }

    public void setImageVisibleListener(VisibleListener visibleListener){
        this.visibleListener =visibleListener;

    }

    public void updateStartImage() {
        if (state == STATE_PLAYING) {
        } else if (state == STATE_ERROR) {

        } else if (state == STATE_AUTO_COMPLETE) {

        } else {
        }
    }

    @Override
    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(jzvdContext).inflate(R.layout.jz_dialog_progress, null);
            mDialogSeekTime = localView.findViewById(R.id.tv_current);
            mDialogTotalTime = localView.findViewById(R.id.tv_duration);
            mDialogIcon = localView.findViewById(R.id.duration_image_tip);
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(" / " + totalTime);
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.jz_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.jz_backward_icon);
        }
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(jzvdContext).inflate(R.layout.jz_dialog_volume, null);
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip);
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume);
            mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar);
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_close_volume);
        } else {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jz_add_volume);
        }
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mDialogVolumeTextView.setText(volumePercent + "%");
        mDialogVolumeProgressBar.setProgress(volumePercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    @Override
    public void showBrightnessDialog(int brightnessPercent) {
        super.showBrightnessDialog(brightnessPercent);
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(jzvdContext).inflate(R.layout.jz_dialog_brightness, null);
            mDialogBrightnessTextView = localView.findViewById(R.id.tv_brightness);
            mDialogBrightnessProgressBar = localView.findViewById(R.id.brightness_progressbar);
            mBrightnessDialog = createDialogWithView(localView);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        mDialogBrightnessTextView.setText(brightnessPercent + "%");
        mDialogBrightnessProgressBar.setProgress(brightnessPercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
    }

    public Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(jzvdContext, R.style.jz_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }

    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        cancelDismissControlViewTimer();
    }

    @Override
    public void reset() {
        super.reset();
        cancelDismissControlViewTimer();
        unregisterWifiListener(getApplicationContext());
    }

    public void dissmissControlView() {
        if (state != STATE_NORMAL
                && state != STATE_ERROR
                && state != STATE_AUTO_COMPLETE) {
            post(() -> {
                cancelProgressTimer();
            });
        }
    }

    public void registerWifiListener(Context context) {
        if (context == null) return;
        mIsWifi = JZUtils.isWifiConnected(context);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(wifiReceiver, intentFilter);
    }

    public void unregisterWifiListener(Context context) {
        if (context == null) return;
        try {
            context.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            dissmissControlView();
        }
    }

}
