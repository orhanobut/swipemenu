package com.orhanobut.android.swipemenu;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RawResourceReader {
    public static String readTextFileFromRawResource(Context context, int resourceId) {

        InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        StringBuilder body = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }
}