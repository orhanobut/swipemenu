package com.orhanobut.android.swipemenu;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    public static final float ROTATE_RADIUS = 2;
    public static final float DELTA_THETA = 15;

    private final Square[] squares = new Square[4];
    private Square backGround;
    private Square backgroundBack;
    private final Context context;
    private final int[] drawOrder = new int[getSquares().length];
    private final float[] PMatrix = new float[16];
    private final float[] VMatrix = new float[16];
    private final float[] tempMatrix = new float[16];
    private final float[] ModelMatrix = new float[16];

    private float jumpDirection = 1f;
    protected final static long VELOCITY = 10;
    private int buzzControl;

    private float direction = 1f;
    private volatile boolean isRotating = true;
    private boolean isLoaded = false;

    private float eyeZ;
    private float atZ;

    public MyRenderer(Context context) {
        super();
        this.context = context;
        setLoaded(false);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(getTempMatrix(), 0, getPMatrix(), 0, getVMatrix(), 0);

        if (isRotating()) {
            update();
        } else {
            // buzzControl++;
            // jump();
        }

        draw();

        try {
            Thread.sleep(VELOCITY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getBackgroundBack().draw();
        getBackGround().draw();

        for (int i : getDrawOrder()) {
            getSquares()[i].draw();
        }

        setLoaded(true);

    }

    public void onUpdateCompleted(boolean isCompleted) {
        for (Square s : getSquares()) {
            s.isCompleted = isCompleted;
        }
    }

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
                0f, 0.0f, 0.0f, 1.0f
        };

        String vertexShader2 = RawResourceReader.readTextFileFromRawResource(
                getContext(), R.raw.vertex_shader);
        String fragmentShader2 = RawResourceReader.readTextFileFromRawResource(
                getContext(), R.raw.fragment_shader);

        Square s1 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s1.setColor(new float[]{0f,0f,0f,1f});
        s1.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_capital);

        s1.setZ(-2.0f);
        s1.theta = MainCircle.FRONT;

        Square s2 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s2.setColor(new float[]{0f,1f,1f,1f});
        s2.setZ(2.0f);
        s2.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_noncapital);
        s2.theta = MainCircle.BACK;

        Square s3 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s3.setColor(new float[]{1f,1f,0f,1f});
        s3.setX(-2.0f);
        s3.textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.pg_number);
        s3.theta = MainCircle.RIGHT;

        Square s4 = new Square(squareVertices, texture, squareDrawOrder,
                vertexShader2, fragmentShader2);
        s4.setColor(new float[]{1f,0f,0f,1f});
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
        float[] background = {-length, 0.0f, -length * offset, // v0
                -length, 0.0f, length * offset, // v1
                length, 0.0f, -length * offset, // v2
                length, 0.0f, length * offset // v3
        };
        setBackGround(new Square(background, texture, squareDrawOrder,
                vertexShader2, fragmentShader2));
        getBackGround().setColor(new float[]{18f / 255f, 51f / 255f, 199f / 255f, 1f});
        getBackGround().setY(-0.5f);
        getBackGround().textureDataHandle = TextureHelper.loadTexture(getContext(),
                R.drawable.floor);

        length = 3.9f;
        float depth = 1.0f;
        float[] backgroundBack = {-length, -length, depth, // v0
                -length, length, depth, // v1
                length, -length, depth, // v2
                length, length, depth
                // v3
        };
        this.setBackgroundBack(new Square(backgroundBack, texture, squareDrawOrder,
                vertexShader2, fragmentShader2));
        this.getBackgroundBack().setColor(new float[]{179f / 255f, 89f / 255f, 171f / 255f, 1f});
        this.getBackgroundBack().textureDataHandle = TextureHelper.loadTexture(
                getContext(), R.drawable.bg_back_b);

    }

    protected void draw() {

        for (int i = 0; i < getSquares().length; i++) {
            Square s = getSquares()[i];
            Matrix.setIdentityM(getModelMatrix(), 0);
            Matrix.translateM(getModelMatrix(), 0, s.getX(), s.getY(), s.getZ());
            Matrix.multiplyMM(s.getMVPMatrix(), 0, getTempMatrix(), 0, getModelMatrix(), 0);
        }

        Matrix.setIdentityM(getModelMatrix(), 0);
        Matrix.translateM(getModelMatrix(), 0, getBackGround().getX(), getBackGround().getY(),
                getBackGround().getZ());
        Matrix.multiplyMM(getBackGround().getMVPMatrix(), 0, getTempMatrix(), 0,
                getModelMatrix(), 0);

        Matrix.setIdentityM(getModelMatrix(), 0);
        Matrix.translateM(getModelMatrix(), 0, getBackgroundBack().getX(),
                getBackgroundBack().getY(), getBackgroundBack().getZ());
        Matrix.multiplyMM(getBackgroundBack().getMVPMatrix(), 0, getTempMatrix(), 0,
                getModelMatrix(), 0);
    }

    protected void update() {
        for (int i = 0; i < getSquares().length; i++) {

            Square s = getSquares()[i];
            s.setRotateY(1f);

            if (!s.isCompleted) {
                s.theta += getDirection() * DELTA_THETA;
                s.theta = s.theta % 360;

                float angle2 = (s.theta);
                double angle = ((angle2 * Math.PI) / 180);
                s.setX((ROTATE_RADIUS * (float) Math.cos(angle)));
                s.setZ((ROTATE_RADIUS * (float) Math.sin(angle)));
            }

            if ((s.theta % 90) == 0) {
                setRotating(false);
                s.isCompleted = true;
            }

            if (s.theta == 0) {
                getDrawOrder()[1] = i; // RIGHT
            } else if (s.theta == 90 || s.theta == -270) {
                getDrawOrder()[0] = i; // BACK
            } else if (Math.abs((double) s.theta) == 180) {
                getDrawOrder()[2] = i; // LEFT
            } else if (s.theta == 270 || s.theta == -90) {
                getDrawOrder()[3] = i; // FRONT
            }
        }

    }

    private void jump() {
        Square s = getSquares()[getDrawOrder()[3]];
        if (buzzControl <= 40) {
            float offset = 0.025f;
            if (s.getY() <= 0) {
                setJumpDirection(1f);
            } else if (s.getY() >= 0.1000f) {
                setJumpDirection(-1f);
            }
            s.setY(s.getY() + getJumpDirection() * offset);
        } else if (buzzControl == 140) {
            buzzControl = 0;
            s.setY(0.0f);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        float near = 1.0f;
        float far = 150.0f;
        float fov = 25;
        float mTop = (float) (Math.tan(fov * Math.PI / 360.0f) * near);
        float mBottom = -mTop;
        float mLeft = -ratio * mBottom;
        float mRight = -ratio * mTop;

        MyGLSurfaceView.width = ((atZ - eyeZ) * (mLeft - mRight)) / near;
        MyGLSurfaceView.width = Math.abs(MyGLSurfaceView.width);

        MyGLSurfaceView.height = ((atZ - eyeZ) * (mTop - mBottom)) / near;
        MyGLSurfaceView.height = Math.abs(MyGLSurfaceView.height);

        Matrix.frustumM(getPMatrix(), 0, mLeft, mRight, mBottom, mTop, near, far);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        setLoaded(false);

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        float eyeX = 0.0f;
        float eyeY = 2.0f;
        eyeZ = -8.0f;
        float atX = 0.0f;
        float atY = -0.5f;
        atZ = 0.0f;
        float upX = 0.0f;
        float upY = 0.1f;
        float upZ = 0.0f;

        Matrix.setLookAtM(getVMatrix(), 0, eyeX, eyeY, eyeZ, atX, atY, atZ, upX,
                upY, upZ);

        init();

    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public void setRotating(boolean isRotating) {
        this.isRotating = isRotating;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public Context getContext() {
        return context;
    }

    public Square[] getSquares() {
        return squares;
    }

    public Square getBackGround() {
        return backGround;
    }

    public void setBackGround(Square backGround) {
        this.backGround = backGround;
    }

    public Square getBackgroundBack() {
        return backgroundBack;
    }

    public void setBackgroundBack(Square backgroundBack) {
        this.backgroundBack = backgroundBack;
    }

    public int[] getDrawOrder() {
        return drawOrder;
    }

    public float[] getPMatrix() {
        return PMatrix;
    }

    public float[] getVMatrix() {
        return VMatrix;
    }

    public float[] getTempMatrix() {
        return tempMatrix;
    }

    public float[] getModelMatrix() {
        return ModelMatrix;
    }

    public float getJumpDirection() {
        return jumpDirection;
    }

    public void setJumpDirection(float jumpDirection) {
        this.jumpDirection = jumpDirection;
    }
}
