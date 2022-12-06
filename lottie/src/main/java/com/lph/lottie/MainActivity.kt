package com.lph.lottie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.TextDelegate
import com.airbnb.lottie.model.KeyPath

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.dynamicTextView)
        val textDelegate = TextDelegate(lottieAnimationView)
        textDelegate.setText("按钮确定","廖鹏辉")
        lottieAnimationView.setTextDelegate(textDelegate)
//        lottieAnimationView.setAnimationFromUrl("https://gd-cowtransfer.static.cowtransfer.com/cowtransfer/cowtransfer/18951/6b3b782b-3315-4816-9834-84ae39f0d57d7031948.zip?response-content-disposition=attachment%3B%20filename%3D111690-celebration-july.zip%3B&auth_key=1660571539-c0dbcf65bc87443285b4e3e71c10cfbe-0-70076ef46670178a6edb5c9bd1659f2f&channel_code=COW_CN_WEB&business_code=COW_TRANSFER&biz_type=1&user_id=1021039933124518951")
//        lottieAnimationView.playAnimation()
//        LottieCompositionFactory.fromUrl(this,"https://assets10.lottiefiles.com/packages/lf20_qzasi9ko.json")
//            .addListener { composition ->
//                lottieAnimationView.setComposition(composition)
//                lottieAnimationView.playAnimation()
//
//
//            }
//        lottieAnimationView.setAnimationFromUrl("https://assets10.lottiefiles.com/packages/lf20_qzasi9ko.json")
//        lottieAnimationView.playAnimation()
//        lottieAnimationView.addLottieOnCompositionLoadedListener {
//            val path = lottieAnimationView.resolveKeyPath(KeyPath("**"))
//            path.forEach {
//                Log.e("廖鹏辉","${it.keysToString()}");
//            }
//        }
//        LottieCompositionFactory.fromUrl(this,"http://yapi.liyuanwu.hhzdev.com/1.zip")
//            .addListener { composition ->
//                lottieAnimationView.setComposition(composition)
//                lottieAnimationView.playAnimation()
//            }.addFailureListener {
//                it.printStackTrace()
//            }

//        LottieCompositionFactory.fromJsonSync(JSONObject, String)
//        LottieComposition.Factory
//            .fromJson(getResources(), json, new OnCompositionLoadedListener() {
//                @Override
//                public void onCompositionLoaded(LottieComposition composition) {
//                    lottieAnimationView.setComposition(composition);
//                    lottieAnimationView.playAnimation();
//                }
//            });

//        lottieAnimationView.setAnimationFromUrl("https://lottiefiles.com/download/public/114808-bb-brick-breathing/zip");
//        lottieAnimationView.playAnimation();

    }
}