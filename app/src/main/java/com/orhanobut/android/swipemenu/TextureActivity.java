package com.orhanobut.android.swipemenu;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by limon on 08.03.2014.
 */
public class TextureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new MyGLSurfaceView(this,new TextureRenderer(this)));
    }
}
