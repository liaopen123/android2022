package com.lph.livephotoview.jz_real;

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
import android.view.Gravity;
import android.view.LayoutInflater;
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

import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZUtils;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JzvdStd extends Jzvd {

    public static long LAST_GET_BATTERYLEVEL_TIME = 0;
    public static int LAST_GET_BATTERYLEVEL_PERCENT = 70;
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;

    public ProgressBar  loadingProgressBar;
    public ImageView posterImageView;
    public PopupWindow clarityPopWindow;

    protected DismissControlViewTimerTask mDismissControlViewTimerTask;
    protected Dialog mProgressDialog;
    protected ProgressBar mDialogProgressBar;
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
                    startButton.performClick();
                    showWifiDialog();
                }
            }
        }
    };
    protected ArrayDeque<Runnable> delayTask = new ArrayDeque<>();

    public JzvdStd(Context context) {
        super(context);
    }

    public JzvdStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        posterImageView = findViewById(cn.jzvd.R.id.poster);
        loadingProgressBar = findViewById(cn.jzvd.R.id.loading);

        if (posterImageView == null) {
            posterImageView = new ImageView(context);
        }
        if (loadingProgressBar == null) {
            loadingProgressBar = new ProgressBar(context);
        }
        posterImageView.setOnClickListener(this);
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
    public int getLayoutId() {
        return cn.jzvd.R.layout.jz_layout_std;
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
//        loadingProgressBar.setVisibility(View.INVISIBLE);
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
        changeUiToError();
    }
    //播放完成
    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        changeUiToComplete();
        cancelDismissControlViewTimer();
    }

    @Override
    public void startVideo() {
        super.startVideo();
        registerWifiListener(getApplicationContext());
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
     if (i == cn.jzvd.R.id.surface_container) {
            clickSurfaceContainer();
            if (clarityPopWindow != null) {
                clarityPopWindow.dismiss();
            }
        }
    }

    protected void clickRetryBtn() {
        if (jzDataSource.urlsMap.isEmpty() || jzDataSource.getCurrentUrl() == null) {
            Toast.makeText(jzvdContext, getResources().getString(cn.jzvd.R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!jzDataSource.getCurrentUrl().toString().startsWith("file") && !
                jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                !JZUtils.isWifiConnected(jzvdContext) && !WIFI_TIP_DIALOG_SHOWED) {
            showWifiDialog();
            return;
        }
        startVideo();
    }

    protected void clickClarity() {
        onCLickUiToggleToClear();

        LayoutInflater inflater = (LayoutInflater) jzvdContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(cn.jzvd.R.layout.jz_layout_clarity, null);

        OnClickListener mQualityListener = v1 -> {
            int index = (int) v1.getTag();

//                this.seekToInAdvance = getCurrentPositionWhenPlaying();
            jzDataSource.currentUrlIndex = index;
//                onStatePreparingChangeUrl();


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
            TextView clarityItem = (TextView) View.inflate(jzvdContext, cn.jzvd.R.layout.jz_layout_clarity_item, null);
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
        clarityPopWindow.setAnimationStyle(cn.jzvd.R.style.pop_animation);
        clarityPopWindow.showAtLocation(textureViewContainer, Gravity.END, 0, 0);
//            int offsetX = clarity.getMeasuredWidth() / 3;
//            int offsetY = clarity.getMeasuredHeight() / 3;
//            clarityPopWindow.update(clarity, -offsetX, -offsetY, Math.round(layout.getMeasuredWidth() * 2), layout.getMeasuredHeight());
    }

    protected void clickBackTiny() {
    }


    protected void clickSurfaceContainer() {
        startDismissControlViewTimer();
    }


    @Override
    public void setScreenNormal() {
        super.setScreenNormal();
    }

    @Override
    public void setScreenFullscreen() {
        super.setScreenFullscreen();
        //进入全屏之后要保证原来的播放状态和ui状态不变，改变个别的ui
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
        builder.setMessage(getResources().getString(cn.jzvd.R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(cn.jzvd.R.string.tips_not_wifi_confirm), (dialog, which) -> {
            dialog.dismiss();
            WIFI_TIP_DIALOG_SHOWED = true;
            if (state == STATE_PAUSE) {
                startButton.performClick();
            } else {
                startVideo();
            }

        });
        builder.setNegativeButton(getResources().getString(cn.jzvd.R.string.tips_not_wifi_cancel), (dialog, which) -> {
            dialog.dismiss();
            releaseAllVideos();
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                releaseAllVideos();
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
                changeUiToPlayingShow();
        } else if (state == STATE_PAUSE) {
                changeUiToPauseShow();
        }
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
        startButton.setVisibility(startBtn);
        loadingProgressBar.setVisibility(loadingPro);
        posterImageView.setVisibility(posterImg);
    }

    public void updateStartImage() {
        if (state == STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(cn.jzvd.R.drawable.jz_click_pause_selector);
        } else if (state == STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
        } else if (state == STATE_AUTO_COMPLETE) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(cn.jzvd.R.drawable.jz_click_replay_selector);
        } else {
            startButton.setImageResource(cn.jzvd.R.drawable.jz_click_play_selector);
        }
    }

    @Override
    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(jzvdContext).inflate(cn.jzvd.R.layout.jz_dialog_progress, null);
            mDialogProgressBar = localView.findViewById(cn.jzvd.R.id.duration_progressbar);
            mDialogSeekTime = localView.findViewById(cn.jzvd.R.id.tv_current);
            mDialogTotalTime = localView.findViewById(cn.jzvd.R.id.tv_duration);
            mDialogIcon = localView.findViewById(cn.jzvd.R.id.duration_image_tip);
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(" / " + totalTime);
        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (int) (seekTimePosition * 100 / totalTimeDuration));
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(cn.jzvd.R.drawable.jz_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(cn.jzvd.R.drawable.jz_backward_icon);
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
            View localView = LayoutInflater.from(jzvdContext).inflate(cn.jzvd.R.layout.jz_dialog_volume, null);
            mDialogVolumeImageView = localView.findViewById(cn.jzvd.R.id.volume_image_tip);
            mDialogVolumeTextView = localView.findViewById(cn.jzvd.R.id.tv_volume);
            mDialogVolumeProgressBar = localView.findViewById(cn.jzvd.R.id.volume_progressbar);
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(cn.jzvd.R.drawable.jz_close_volume);
        } else {
            mDialogVolumeImageView.setBackgroundResource(cn.jzvd.R.drawable.jz_add_volume);
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
            View localView = LayoutInflater.from(jzvdContext).inflate(cn.jzvd.R.layout.jz_dialog_brightness, null);
            mDialogBrightnessTextView = localView.findViewById(cn.jzvd.R.id.tv_brightness);
            mDialogBrightnessProgressBar = localView.findViewById(cn.jzvd.R.id.brightness_progressbar);
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
        Dialog dialog = new Dialog(jzvdContext, cn.jzvd.R.style.jz_style_dialog_progress);
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
                startButton.setVisibility(View.INVISIBLE);

                if (screen != SCREEN_TINY) {
                }
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
