package com.example.semestralka

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val imageView = findViewById<ImageView>(R.id.imageView)

        btnBack.setOnClickListener {
            finish()
        }

        val latex = intent.getStringExtra("latex")
        if (latex != null) {
            val url = "https://math.vercel.app/?from=$latex"
            Log.d("RenderActivity", "Fetching URL: $url")
            RenderTask(imageView).execute(url)
        }
    }

    private class RenderTask(val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            return try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                Log.d("RenderTask", "Connection established")

                val inputStream = connection.inputStream
                val svg = SVG.getFromInputStream(inputStream)
                val picture = svg.renderToPicture()
                Log.d("RenderTask", "SVG rendered to picture")
                pictureToBitmap(picture)
            } catch (e: Exception) {
                Log.e("RenderTask", "Error fetching or rendering SVG", e)
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            if (result != null) {
                Log.d("RenderTask", "Bitmap successfully created")
                imageView.setImageBitmap(result)
            } else {
                Log.e("RenderTask", "Failed to create bitmap")
            }
        }

        private fun pictureToBitmap(picture: Picture): Bitmap {
            val width = picture.width.takeIf { it > 0 } ?: 1
            val height = picture.height.takeIf { it > 0 } ?: 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawPicture(picture)
            return bitmap
        }
    }
}