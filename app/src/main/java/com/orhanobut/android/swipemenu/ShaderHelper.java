package com.orhanobut.android.swipemenu;

import android.opengl.GLES20;

public class ShaderHelper {

    public static int initShader(String vertexShaderCode, String fragmentShaderCode) {
        int vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        int programHandle = GLES20.glCreateProgram();

        GLES20.glAttachShader(programHandle, vertexShaderHandle);
        GLES20.glAttachShader(programHandle, fragmentShaderHandle);
        GLES20.glBindAttribLocation(programHandle, 0, "vPosition");

        GLES20.glLinkProgram(programHandle);
        return programHandle;
    }

    private static int loadShader(int type, String shaderCode) {
        int shader;

        // create shader object
        shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            return 0;
        }

        // load shader
        GLES20.glShaderSource(shader, shaderCode);

        // compile shader
        GLES20.glCompileShader(shader);

        return shader;
    }
}
