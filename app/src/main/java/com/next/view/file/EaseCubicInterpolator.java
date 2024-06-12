package com.next.view.file;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * ClassName:缓动三次方曲线插值器类
 *
 * @author Afton
 * @time 2024/4/16
 * @auditor
 */
public class EaseCubicInterpolator implements Interpolator {

    //精度
    private final static int ACCURACY = 4096;

    //上次插值计算时传入的时间
    private int mLastI = 0;

    //控制点
    private final PointF mControlPoint1 = new PointF();

    //控制点
    private final PointF mControlPoint2 = new PointF();

    public EaseCubicInterpolator(float x1, float y1, float x2, float y2) {
        this.mControlPoint1.x = x1;
        this.mControlPoint1.y = y1;
        this.mControlPoint2.x = x2;
        this.mControlPoint2.y = y2;
    }

    @Override
    public float getInterpolation(float input) {
        float t = input;
        for (int i = this.mLastI; i < this.ACCURACY; i++) {
            t = 1.0f * i / this.ACCURACY;
            double x = cubicCurves(t, 0, this.mControlPoint1.x, this.mControlPoint2.x, 1);
            if (x >= input) {
                this.mLastI = i;
                break;
            }
        }

        double value = cubicCurves(t, 0, this.mControlPoint1.y, this.mControlPoint2.y, 1);

        if (value > 0.999d) {
            value = 1;
            this.mLastI = 0;
        }

        return (float) value;
    }

    /**
     * 求三次贝塞尔曲线(四个控制点)一个点某个维度的值
     *
     * @param t      插值比例
     * @param value0 起点
     * @param value1 控制点1
     * @param value2 控制点2
     * @param value3 终点
     * @return 值
     */
    public static double cubicCurves(double t, double value0, double value1, double value2, double value3) {
        double value;
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;
        value = uuu * value0;
        value += 3 * uu * t * value1;
        value += 3 * u * tt * value2;
        value += ttt * value3;
        return value;
    }
}