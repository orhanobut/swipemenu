package com.orhanobut.android.swipemenu;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by limon on 08.03.2014.
 */
public class TextureRenderer extends MyRenderer {

    public TextureRenderer(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        float length = 0.5f;
        float squareVertices[] = {
                -length, -length, 0.0f, // V0 - bottom left
                -length, length, 0.0f, // V1 - top left
                length, -length, 0.0f, // V2 - bottom right
                length, length, 0.0f   // V3 - top right
        };

        // Mapping coordinates for the vertices
        float texture[] = {
                0.0f, 1.0f, // top left (V2)
                0.0f, 0.0f, // bottom left (V1)
                1.0f, 1.0f, // top right (V4)
                1.0f, 0.0f  // bottom right (V3)
        };

        short[] squareDrawOrder = {
                0, 1, 2, // front face
                2, 1, 3,
        };

        float[] color = {
                1f, 0.0f, 0.0f, 1.0f
        };

        String vertexShader2 = RawResourceReader.readTextFileFromRawResource(
                getContext(), R.raw.vertex_shader_texture);
        String fragmentShader2 = RawResourceReader.readTextFileFromRawResource(
                getContext(), R.raw.fragment_shader_texture);

        Square s1 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s1.setColor(color);
        s1.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_capital);

        s1.setZ(-2.0f);
        s1.theta = MainCircle.FRONT;

        Square s2 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s2.setColor(color);
        s2.setZ(2.0f);
        s2.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_noncapital);
        s2.theta = MainCircle.BACK;

        Square s3 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s3.setColor(color);
        s3.setX(-2.0f);
        s3.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_number);
        s3.theta = MainCircle.RIGHT;

        Square s4 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s4.setColor(color);
        s4.setX(2.0f);
        s4.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_game);
        s4.theta = MainCircle.LEFT;

        getSquares()[0] = s3; // BACK
        getSquares()[1] = s4; // RIGHT
        getSquares()[2] = s2; // LEFT
        getSquares()[3] = s1; // FRONT

        length = 3.5f;
        float offset = 1.0f;
        float[] background = {
                -length, 0.0f, -length * offset, // v0
                -length, 0.0f, length * offset, // v1
                length, 0.0f, -length * offset, // v2
                length, 0.0f, length * offset // v3
        };
        setBackGround(new Square(background, texture, squareDrawOrder,
                vertexShader2, fragmentShader2));
        getBackGround().setColor(color);
        getBackGround().setY(-0.5f);
        getBackGround().textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.floor);

        length = 3.9f;
        float depth = 1.0f;
        float[] backgroundBack = {
                -length, -length, depth, // v0
                -length, length, depth, // v1
                length, -length, depth, // v2
                length, length, depth // v3
        };
        setBackgroundBack(new Square(backgroundBack, texture, squareDrawOrder,
                vertexShader2, fragmentShader2));
        getBackgroundBack().setColor(color);
        getBackgroundBack().textureDataHandle = TextureHelper.loadTexture(
                getContext(), R.drawable.bg_back_b);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(getTempMatrix(), 0, getPMatrix(), 0, getVMatrix(), 0);

        if (isRotating()) {
            update();
        } else {
            setBuzzControl(getBuzzControl()+1);
            jump();
        }

        refreshModelMatrices();

        try {
            Thread.sleep(VELOCITY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getBackgroundBack().drawWithTexture();
        getBackGround().drawWithTexture();

        for (int i : getDrawOrder()) {
            getSquares()[i].drawWithTexture();
        }

        setLoaded(true);

    }
}
