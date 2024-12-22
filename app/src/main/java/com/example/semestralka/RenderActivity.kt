package com.example.semestralka

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
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
        val scale = intent.getIntExtra("scale", 10)
        if (latex != null) {
            val url = "https://math.vercel.app/?from=$latex"
            Log.d("RenderActivity", "Fetching URL: $url with scale: $scale")
            RenderTask(imageView, scale).execute(url)
        }
    }

    private class RenderTask(val imageView: ImageView, val scale: Int) : AsyncTask<String, Void, SVG?>() {
        override fun doInBackground(vararg params: String?): SVG? {
            return try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                Log.d("RenderTask", "Connection established")

                val inputStream = connection.inputStream
                val svg = SVG.getFromInputStream(inputStream)
                Log.d("RenderTask", "SVG fetched")
                svg
            } catch (e: Exception) {
                Log.e("RenderTask", "Error fetching SVG", e)
                null
            }
        }

        override fun onPostExecute(svg: SVG?) {
            super.onPostExecute(svg)
            if (svg != null) {
                Log.d("RenderTask", "SVG successfully fetched")
                imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
                imageView.setImageDrawable(SVGDrawable(svg, scale))
            } else {
                Log.e("RenderTask", "Failed to fetch SVG")
            }
        }
    }

    private class SVGDrawable(val svg: SVG, val scale: Int) : android.graphics.drawable.Drawable() {
        override fun draw(canvas: android.graphics.Canvas) {
            val width = canvas.width / scale
            val height = canvas.height / scale
            svg.documentWidth = width.toFloat()
            svg.documentHeight = height.toFloat()
            svg.renderToCanvas(canvas)
        }

        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
        override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
    }
}