package com.hhz.test.guide;


import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CoverDrawable extends Drawable {
    private Drawable drawable;
    private Paint paint;
    private Path path = new Path();
    private Path path1 = new Path();

    public CoverDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xffffffff);
    }

    /**
     * 绘制圆
     *
     * @param drawable
     * @param x
     * @param y
     * @param radius
     */
    public CoverDrawable(@NonNull Drawable drawable,
                         int x, int y, int radius) {
        this(drawable);
//        path.addCircle(x, y, radius, Path.Direction.CW);
        path1.addRoundRect(50, 10, 300, 150, 100, 100, Path.Direction.CW);
    }

    /**
     * 绘制圆角矩形
     *
     * @param drawable
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param rx
     * @param ry
     */
    public CoverDrawable(@NonNull Drawable drawable,
                         int left, int top, int right, int bottom, int rx, int ry) {
        this(drawable);
        path.addRoundRect(left, top, right, bottom, rx, ry, Path.Direction.CW);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        drawable.setBounds(getBounds());
        if (path == null || path.isEmpty()) {
            drawable.draw(canvas);
        } else {
            //将绘制操作保存在新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
            int saveCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), paint, Canvas.ALL_SAVE_FLAG);
            //绘制目标图
            drawable.draw(canvas);
            //设置混合模式
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            //绘制原图
            canvas.drawPath(path, paint);
            canvas.drawPath(path1, paint);
            //清除混合模式
            paint.setXfermode(null);
            //还原画布
            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    public void setAlpha(int i) {
        drawable.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        drawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return drawable.getOpacity();
    }
}
