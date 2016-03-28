package com.orionedwards.mtglifecounter;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DataStore {
    public static JSONObject getWithKey(Context context, String key) throws DataStoreException {
        FileInputStream inputStream = null;
        try {
            inputStream = context.openFileInput(key + ".json");
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\Z");
            String data = scanner.next();

            return new JSONObject(data);

        } catch (FileNotFoundException | JSONException | NoSuchElementException e) {
            throw new DataStoreException(e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException unused) {} // what on earth can we do with an IOException upon file close
        }
    }

    public static void setWithKey(Context context, String key, JSONObject value) throws DataStoreException {
        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            outputStream = context.openFileOutput(key + ".json", Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(value.toString());
        } catch (IOException e) {
            throw new DataStoreException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException unused){ }
        }
    }
}

class DataStoreException extends Exception {
    public DataStoreException(Exception inner) {
        super("data store exception", inner);
    }
}
