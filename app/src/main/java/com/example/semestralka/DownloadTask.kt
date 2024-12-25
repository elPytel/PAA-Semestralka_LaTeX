package com.example.semestralka

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import com.caverock.androidsvg.SVG
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadTask(private val context: Context, private val equation: Equation) {
    private var svgContent: String? = null

    fun execute(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val svg = doInBackground(url)
            withContext(Dispatchers.Main) {
                onPostExecute(svg)
            }
        }
    }

    private suspend fun doInBackground(vararg params: String): SVG? {
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

    private fun onPostExecute(svg: SVG?) {
        if (svg != null) {
            Log.d("DownloadTask", "SVG successfully fetched")
            equation.setSvgContent(svgContent!!)
            if (equation.svgFileName != null) {
                equation.saveSVG2File(context, equation.svgFileName!!)
            }
            equation.displaySvg()
        } else {
            Log.e("DownloadTask", "Failed to fetch SVG")
        }
    }
}
