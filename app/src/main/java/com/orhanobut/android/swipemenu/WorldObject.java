
package com.orhanobut.android.swipemenu;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class WorldObject {

    private float x;
    private float y;
    private float z;
    private float rotateY;
    private float[] color;
    private short[] drawOrder;
    private float[] vertices;
    private float[] MVPMatrix = new float[16];

    private int programHandle;
    private FloatBuffer modelCoordinatesBuffer;
    private int vertexStride;
    private int COORDS_PER_VERTEX = 3;
    private ShortBuffer drawListBuffer;

    /**
     * This will be used to pass in the texture.
     */
    private int textureUniformHandle;

    /**
     * This will be used to pass in model texture coordinate information.
     */
    private int textureCoordinateHandle;

    /**
     * Size of the texture coordinate data in elements.
     */
    private final int textureCoordinateDataSize = 2;

    /**
     * This is a handle to our texture data.
     */
    public int textureDataHandle;

    public float[] textureCoordinates;
    public FloatBuffer textureBuffer;

    final float[] cubeTextureCoordinateData = {
            // Front face

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

    };

    public WorldObject(float[] vertices, short[] drawOrder, String vertexShaderCode,
                       String fragmentShaderCode) {
        this.setVertices(vertices);
        this.setDrawOrder(drawOrder);
        init();
        programHandle = ShaderHelper.initShader(vertexShaderCode, fragmentShaderCode);
        initVertex();
    }

    public WorldObject(float[] vertices, float[] texture, short[] drawOrder, String vertexShaderCode,
                       String fragmentShaderCode) {
        this.setVertices(vertices);
        this.setDrawOrder(drawOrder);
        this.textureCoordinates = texture;
        init();
        programHandle = ShaderHelper.initShader(vertexShaderCode, fragmentShaderCode);
        initVertex();
    }

    public abstract void init();

    public void initVertex() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                getVertices().length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        modelCoordinatesBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        modelCoordinatesBuffer.put(getVertices());
        // set the buffer to read the first coordinate
        modelCoordinatesBuffer.position(0);

        vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                getDrawOrder().length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(getDrawOrder());
        drawListBuffer.position(0);

        if (textureCoordinates != null) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            textureBuffer = byteBuffer.asFloatBuffer();
            textureBuffer.put(textureCoordinates);
            textureBuffer.position(0);
        }
    }

    public void draw() {

        GLES20.glUseProgram(programHandle);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(programHandle,
                "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // load vertex data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, modelCoordinatesBuffer);

        // get handle to fragment shader's vColor member
        int mColorHandle = GLES20
                .glGetUniformLocation(programHandle, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, getColor(), 0);

        // get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, getMVPMatrix(), 0);

        // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, getDrawOrder().length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void drawWithTexture() {

        GLES20.glUseProgram(programHandle);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(programHandle,
                "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // load vertex data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, modelCoordinatesBuffer);

        // get handle to fragment shader's vColor member
        int mColorHandle = GLES20
                .glGetUniformLocation(programHandle, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, getColor(), 0);

        textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate");
        textureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");


        GLES20.glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, textureBuffer);

        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);


        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureUniformHandle, 0);


        // get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, getMVPMatrix(), 0);


        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, getDrawOrder().length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getRotateY() {
        return rotateY;
    }

    public void setRotateY(float rotateY) {
        this.rotateY = rotateY;
    }

    public float[] getColor() {
        return color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public short[] getDrawOrder() {
        return drawOrder;
    }

    public void setDrawOrder(short[] drawOrder) {
        this.drawOrder = drawOrder;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public float[] getMVPMatrix() {
        return MVPMatrix;
    }

    public void setMVPMatrix(float[] MVPMatrix) {
        this.MVPMatrix = MVPMatrix;
    }
}
