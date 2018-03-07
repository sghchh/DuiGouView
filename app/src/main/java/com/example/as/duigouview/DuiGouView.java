package com.example.as.duigouview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.ScaleAnimation;

/**
 * Created by as on 2018/3/6.
 */

public class DuiGouView extends View {
    //保存当前按钮的状态，默认为false代表是灰色的时候，true代表被点击过了为激活状态
    private boolean status=false;
    //默认的颜色
    private final int DEFAULT_COLOR= Color.YELLOW;
    //默认的半径（px）
    private final float DEFAULT_RADIUS=256;

    //接受自定义属性中的  颜色
    private int color;
    //外层白色实心圆的半径，属性动画的时候要用到，第一次要把它加载成属性中的半径，保证正好盖住颜色区域
    private float radiusWhite;
    //接受属性传来的  半径  参数
    private float radius;
    //有颜色的画笔
    private Paint colorPaint;
    //专门画白色区域的画笔
    private Paint whitePaint;
    //画为点击状态的灰色
    private Paint grayPaint;

    //话圆环动画的Paint
    private Paint ringPaint;

    //画圆弧属性动画的属性，通过更改圆弧的度数来完成，需要为其提供setter和getter方法
    private int degree;
    public DuiGouView(Context context) {
        this(context,null);
    }

    public DuiGouView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(context,attrs);
        initPaints();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatusToLive();
                addAnimator();
            }
        });
    }

    /**
     * 初始化属性
     * @param attributeSet
     */
    private void initValue(Context context,AttributeSet attributeSet){
        TypedArray array=context.obtainStyledAttributes(attributeSet,R.styleable.DuiGouView);
        color=array.getColor(R.styleable.DuiGouView_color,DEFAULT_COLOR);
        radius=array.getDimension(R.styleable.DuiGouView_radius,DEFAULT_RADIUS);
        radiusWhite=radius;
        degree=0;
    }

    /**
     * 初始化画笔对象
     */

    private void initPaints(){
        colorPaint=new Paint();
        colorPaint.setColor(color);
        colorPaint.setStyle(Paint.Style.FILL);
        colorPaint.setAntiAlias(true);

        whitePaint=new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.FILL);

        grayPaint=new Paint();
        grayPaint.setStyle(Paint.Style.STROKE);
        grayPaint.setStrokeWidth(8);
        grayPaint.setAntiAlias(true);
        grayPaint.setColor(Color.GRAY);

        ringPaint=new Paint();
        ringPaint.setStrokeWidth(4);
        ringPaint.setColor(color);
        ringPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width=MeasureSpec.getSize(widthMeasureSpec);
        float height=MeasureSpec.getSize(heightMeasureSpec);
        height=height>=radius*2?height:radius*2;
        width=width>=radius*2?width:radius*2;
        setMeasuredDimension((int)width,(int)height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //测量的宽和高
        float width=getMeasuredWidth();
        float height=getMeasuredHeight();

        //对勾的三个端点
        float[] points=new float[]{width/2-radius/3,height/2,5*width/12+radius/6,height/2+radius/3,5*radius/3,height/2-radius*5/12};

        //先画出带颜色的圆，然后用白色的圆覆盖住,最后画出灰色的圆
        canvas.drawCircle(width/2,height/2,radius,colorPaint);
        canvas.drawCircle(width/2,height/2,radiusWhite,whitePaint);
        if(!status)
            canvas.drawCircle(width/2,height/2,radius,grayPaint);

        //画对勾
        if (!status)
            drawDuiGou(canvas,points);
        //canvas.drawArc(getLeft(),getTop(),getRight(),getBottom(),0,degree,false,ringPaint);
        canvas.drawArc(4,4,getMeasuredWidth()-4,getMeasuredHeight()-4,0,degree,false,ringPaint);

    }

    /**
     * 画对勾
     * @param canvas 画布
     * @param points 三个点
     */
    private void drawDuiGou(Canvas canvas,float[] points){
        Path path=new Path();
        path.moveTo(points[0],points[1]);
        path.lineTo(points[2],points[3]);
        path.lineTo(points[4],points[5]);
        canvas.drawPath(path,grayPaint);
    }

    /**
     * 属性动画，完成画圈和环的动画
     */
    private void addAnimator(){
        AnimatorSet set=new AnimatorSet();
        ObjectAnimator animator=ObjectAnimator.ofInt(this,"degree",0,360);
        ObjectAnimator a =ObjectAnimator.ofFloat(this,"radiusWhite",radiusWhite,0.0f);
        set.playSequentially(animator,a);
        set.setDuration(1000);
        set.start();

        //增加动画监听，在属性动画进行完后进行View动画
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                addScaleAni();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 添加缩放动画
     */
    private void addScaleAni(){
        setStatusToDead();
        ScaleAnimation scaleAnimation=new ScaleAnimation(1,1.2f,1,1.2f,getMeasuredWidth()/2,getMeasuredHeight()/2);
        scaleAnimation.setDuration(350);
        this.startAnimation(scaleAnimation);
    }

    /**
     * radiusWhite的setter方法
     * @param r
     */
    private void setRadiusWhite(float r){
        this.radiusWhite=r;
        invalidate();
    }

    /**
     * radiusWhite的getter方法
     * @return
     */
    private float getRadiusWhite(){
        return this.radiusWhite;
    }

    /**
     * 圆弧属性动画对应的属性--度  的setter方法
     * @param degree
     */
    private void setDegree(int degree){
        this.degree=degree;
        invalidate();
    }

    /**
     * 圆弧属性动画对应的属性--度 的getter方法
     * @return
     */
    private int getDegree(){
        return this.degree;
    }
    /**
     * 返回当前按钮的状态
     * @return
     */
    private boolean getStatus(){
        return this.status;
    }

    /**
     * 点击后，将status状态置为true
     */
    private void setStatusToLive(){
        this.status=true;
    }

    /**
     * 点击后，将status状态置为false
     */
    private void setStatusToDead(){
        this.status=false;
    }
}
