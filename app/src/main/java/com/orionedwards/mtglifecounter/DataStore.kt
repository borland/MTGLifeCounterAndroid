package com.orionedwards.mtglifecounter

import android.content.Context

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.NoSuchElementException
import java.util.Scanner

object DataStore {
    @Throws(DataStoreException::class)
    fun getWithKey(context: Context, key: String): JSONObject {
        var inputStream: FileInputStream? = null
        try {
            inputStream = context.openFileInput("$key.json")
            val scanner = Scanner(inputStream).useDelimiter("\\Z")
            val data = scanner.next()

            return JSONObject(data)

        } catch (e: FileNotFoundException) {
            throw DataStoreException(e)
        } catch (e: JSONException) {
            throw DataStoreException(e)
        } catch (e: NoSuchElementException) {
            throw DataStoreException(e)
        } finally {
            try {
                inputStream?.close()
            } catch (unused: IOException) {
            }
            // what on earth can we do with an IOException upon file close
        }
    }

    @Throws(DataStoreException::class)
    fun setWithKey(context: Context, key: String, value: JSONObject) {
        var outputStream: FileOutputStream? = null
        var writer: OutputStreamWriter? = null
        try {
            outputStream = context.openFileOutput("$key.json", Context.MODE_PRIVATE)
            writer = OutputStreamWriter(outputStream)
            writer.write(value.toString())
        } catch (e: IOException) {
            throw DataStoreException(e)
        } finally {
            try {
                writer?.close()
                outputStream?.close()
            } catch (unused: IOException) {
            }

        }
    }
}

internal class DataStoreException(inner: Exception) : Exception("data store exception", inner)
