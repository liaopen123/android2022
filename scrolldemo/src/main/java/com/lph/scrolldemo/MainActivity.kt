package com.lph.scrolldemo

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun notify(view: View) {
        //1.获取消息服务
        //1.获取消息服务
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //2.实例化通知
        //2.实例化通知
        val nc: NotificationCompat.Builder = NotificationCompat.Builder(this, "default")
        //通知默认的声音 震动 呼吸灯
        //通知默认的声音 震动 呼吸灯
        nc.setDefaults(NotificationCompat.DEFAULT_ALL)
        //通知标题
        //通知标题
        nc.setContentTitle("标题")
        //通知内容
        //通知内容
        nc.setContentText("内容")
        //设置通知的小图标
        //设置通知的小图标
        nc.setSmallIcon(android.R.drawable.ic_popup_reminder)
        //设置通知的大图标
        //设置通知的大图标
        nc.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        //设定通知显示的时间
        //设定通知显示的时间
        nc.setWhen(System.currentTimeMillis())
        //设置通知的优先级
        //设置通知的优先级
        nc.setPriority(NotificationCompat.PRIORITY_MAX)
        //设置点击通知之后通知是否消失
        //设置点击通知之后通知是否消失
        nc.setAutoCancel(true)
        //点击通知打开软件，使用PendingIntent.getActivity()
        //点击通知打开软件，使用PendingIntent.getActivity()
        val application: Context = applicationContext
        val resultIntent = Intent(application, MainActivity::class.java)
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(application, 0, resultIntent, FLAG_IMMUTABLE)
        nc.setContentIntent(pendingIntent)
        //3.创建通知，得到build
        //3.创建通知，得到build
        val notification: Notification = nc.build()
        //4.发送通知
        //4.发送通知
        manager.notify(1, notification)
    }
}