
package com.orhanobut.android.swipemenu;


public class Square extends WorldObject {

    public float theta;
    public boolean isCompleted = true;

    public Square(float[] vertices, short[] drawOrder, String vertexShaderCode,
                  String fragmentShaderCode) {
        super(vertices, drawOrder, vertexShaderCode, fragmentShaderCode);
    }

    public Square(float[] vertices, float[] texture, short[] drawOrder, String vertexShaderCode,
                  String fragmentShaderCode) {
        super(vertices, texture, drawOrder, vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void init() {

    }
}
