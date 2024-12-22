package com.example.semestralka

import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import java.net.HttpURLConnection
import java.net.URL

class DownloadTask(private val imageView: ImageView, private val scale: Int) : AsyncTask<String, Void, SVG?>() {
    private var svgContent: String? = null

    override fun doInBackground(vararg params: String?): SVG? {
        return try {
            val url = URL(params[0])
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            Log.d("DownloadTask", "Connection established")

            val inputStream = connection.inputStream
            svgContent = inputStream.bufferedReader().use { it.readText() }
            val svg = SVG.getFromString(svgContent)
            Log.d("DownloadTask", "SVG fetched")
            svg
        } catch (e: Exception) {
            Log.e("DownloadTask", "Error fetching SVG", e)
            null
        }
    }

    override fun onPostExecute(svg: SVG?) {
        super.onPostExecute(svg)
        if (svg != null) {
            Log.d("DownloadTask", "SVG successfully fetched")
            (imageView.context as RenderActivity).svgContent = svgContent
            (imageView.context as RenderActivity).displaySvg(svg, scale)
        } else {
            Log.e("DownloadTask", "Failed to fetch SVG")
        }
    }
}