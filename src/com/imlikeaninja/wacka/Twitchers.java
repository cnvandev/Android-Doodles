package com.imlikeaninja.wacka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class Twitchers extends View {
    
    public static final float STROKE_WIDTH = 10;
    public static final float TWITCHER_VELOCITY = 500;
    
    int width = 0;
    int height = 0;
    
    class Twitcher {
        static final int HORIZONTAL = 0;
        static final int VERTICAL = 1;
        
        static final int POSITIVE = 0;
        static final int NEGATIVE = 1;
        
        int orientation = 0;
        int direction = 0;
        
        float x = 0;
        float y = 0;

        float lastX = 0;
        float lastY = 0;

        float velocity_x = 0;
        float velocity_y = 0;

        public Twitcher(int newOrientation, int newDirection, float initialX, float initialY) {
            this.orientation = newOrientation;
            this.direction = newDirection;
            this.x = initialX;
            this.y = initialY;
            this.lastX = initialX;
            this.lastY = initialY;

            if (this.orientation == Twitcher.HORIZONTAL) {
                if (this.direction == Twitcher.POSITIVE) {
                    this.velocity_x = TWITCHER_VELOCITY;
                } else if (this.direction == Twitcher.NEGATIVE) {
                    this.velocity_x = -TWITCHER_VELOCITY;
                }
            } else {
                if (this.direction == Twitcher.POSITIVE) {
                    this.velocity_y = TWITCHER_VELOCITY;
                } else if (this.direction == Twitcher.NEGATIVE) {
                    this.velocity_y = -TWITCHER_VELOCITY;
                }
            }
        }
        
        public Twitcher(int newOrientation, int newDirection) {
            this.orientation = newOrientation;
            this.direction = newDirection;
            
            // Set an appropriate start position.
            if (this.orientation == Twitcher.HORIZONTAL) {
                this.y = height / 2;
                
                if (this.direction == Twitcher.POSITIVE) {
                    this.x = 0;
                    this.velocity_x = TWITCHER_VELOCITY;
                } else if (this.direction == Twitcher.NEGATIVE) {
                    this.x = width;
                    this.velocity_x = -TWITCHER_VELOCITY;
                }
            } else if (this.orientation == Twitcher.VERTICAL) {
                this.x = width / 2;
                if (this.direction == Twitcher.POSITIVE) {
                    this.y = 0;
                    this.velocity_x = TWITCHER_VELOCITY;
                } else  if (this.direction == Twitcher.NEGATIVE) {
                    this.y = height;
                    this.velocity_x = -TWITCHER_VELOCITY;
                }
            }

            this.lastX = this.x;
            this.lastY = this.y;
        }
        
        public void nextStep(float elapsedTime) {
            this.lastX = this.x;
            this.lastY = this.y;

            this.x += this.velocity_x * elapsedTime;
            this.y += this.velocity_y * elapsedTime;
        }
        
        public void setVelocity(float newVelocity) {
            this.velocity_x = (this.direction == Twitcher.HORIZONTAL)? newVelocity : 0;
            this.velocity_y = (this.direction == Twitcher.VERTICAL)? newVelocity : 0;
        }
    }
    
    ArrayList<Twitcher> twitchers = new ArrayList<Twitcher>();
    
    Paint twitcherPaint;
    
    Bitmap output;
    Canvas canvas;

    public Twitchers(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        twitcherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        twitcherPaint.setStyle(Paint.Style.STROKE);
        twitcherPaint.setStrokeWidth(STROKE_WIDTH);
        twitcherPaint.setColor(Color.RED);
    }
    
    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(output);

        for (int i = 0; i < 10; i++) {
            Twitcher newTwitcher = new Twitcher(Twitcher.HORIZONTAL, Twitcher.POSITIVE, 0, (float) Math.random() * height);
            newTwitcher.setVelocity((float) Math.random() * TWITCHER_VELOCITY);
            twitchers.add(newTwitcher);
        }

        setMeasuredDimension(width, height);
    }
    
    @Override public void onDraw(Canvas viewCanvas) {
        long startTime = System.nanoTime();
        for (Twitcher twitcher : twitchers) {
            canvas.drawLine(twitcher.lastX, twitcher.lastY, twitcher.x, twitcher.y, twitcherPaint);
        }
        viewCanvas.drawBitmap(output, 0, 0, null);
        invalidate();
        
        long endTime = System.nanoTime();
        float elapsedTime = ((float) (endTime - startTime)) / (1*1000*1000*1000f); // From nanoseconds into seconds.

        for (int i = 0; i < twitchers.size(); i++) {
            Twitcher twitcher = twitchers.get(i);

            twitcher.nextStep(elapsedTime);
        }
    }
}
