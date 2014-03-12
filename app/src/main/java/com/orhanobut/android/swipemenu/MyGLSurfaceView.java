
package com.orhanobut.android.swipemenu;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class MyGLSurfaceView extends GLSurfaceView {

    private MyRenderer renderer;
    public static float WIDTH;
    public static float HEIGHT;
    private float previousX;
    private boolean rotate = false;

    public MyGLSurfaceView(Context context, MyRenderer renderer) {
        super(context);
        setEGLContextClientVersion(2);
        this.renderer = renderer;
        setRenderer(renderer);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new MyRenderer(context);
        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!renderer.isLoaded()) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = x;
                rotate = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float diff = x - previousX;
                if (Math.abs(diff) > 55) {
                    rotate = true;
                    if ((x - previousX) < 0) {
                        renderer.setDirection(-1f);
                    } else {
                        renderer.setDirection(1f);
                    }
                } else {
                    rotate = false;
                    renderer.setRotating(false);
                }

                break;
            case MotionEvent.ACTION_UP:
                renderer.setRotating(rotate);
                renderer.onUpdateCompleted(!rotate);
                if (rotate) {
                    //
                }

                if (!rotate && checkIfTouchFocused(x, y)) {
                    //rotate
                }

                break;
        }
        return true;
    }

    private boolean checkIfTouchFocused(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        float x2 = (x * MyGLSurfaceView.WIDTH) / (float) width;
        float y2 = (y * MyGLSurfaceView.HEIGHT) / (float) height;

        if ((x2 >= ((MyGLSurfaceView.WIDTH - 1) / 2)) && (x2 <= ((MyGLSurfaceView.WIDTH + 1) / 2))) {
            if ((y2 >= ((MyGLSurfaceView.HEIGHT - 1) / 2)) && (y2 <= ((MyGLSurfaceView.HEIGHT + 1) / 2))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
