package com.example.semestralka

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class RenderActivity : AppCompatActivity() {

    var svgContent: String? = null
    private var currentFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val etDescription = findViewById<EditText>(R.id.etDescription)

        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveSvgToFile(etDescription.text.toString())
        }

        btnDelete.setOnClickListener {
            deleteSvgFile()
        }

        // Get svg image and scale from intent
        val fileName = intent.getStringExtra("fileName")
        val scale = intent.getIntExtra("scale", 10)

        if (fileName != null) {
            currentFileName = fileName
            val svg = loadSvgFromFile(fileName)
            val description = loadDescriptionFromFile(fileName)
            etDescription.setText(description)
            displaySvg(svg, scale)
        } else {
            val latex = intent.getStringExtra("latex")
            if (latex != null) {
                val url = "https://math.vercel.app/?from=$latex"
                Log.d("RenderActivity", "Fetching URL: $url with scale: $scale")
                DownloadTask(imageView, scale).execute(url)
            }
        }
    }

    private fun loadSvgFromFile(fileName: String): SVG {
        return try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val inputStream = FileInputStream(file)
            svgContent = inputStream.bufferedReader().use { it.readText() }
            val svg = SVG.getFromString(svgContent)
            inputStream.close()
            svg
        } catch (e: Exception) {
            Log.e("RenderActivity", "Error loading SVG from file", e)
            Toast.makeText(this, "Failed to load SVG", Toast.LENGTH_LONG).show()
            SVG.getFromString("")
        }
    }

    private fun loadDescriptionFromFile(fileName: String): String {
        return try {
            val descFileName = fileName.replace(".svg", "_description.txt")
            val descFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName)
            val descInputStream = FileInputStream(descFile)
            val description = descInputStream.bufferedReader().use { it.readText() }
            descInputStream.close()
            description
        } catch (e: Exception) {
            Log.e("RenderActivity", "Error loading description from file", e)
            ""
        }
    }

    fun displaySvg(svg: SVG, scale: Int) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
        imageView.setImageDrawable(SVGDrawable(svg, scale)) 
        Log.d("RenderActivity", "SVG and description loaded from file")
    }

    private fun saveSvgToFile(description: String) {
        if (svgContent != null) {
            try {
                val fileName = generateFileName("svg")
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                val fos = FileOutputStream(file)
                fos.write(svgContent!!.toByteArray())
                fos.close()
                Log.i("RenderActivity", "SVG file: ${fileName}, saved to ${file.absolutePath}")

                val descFileName = generateFileName("txt")
                val descFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName)
                val descFos = FileOutputStream(descFile)
                descFos.write(description.toByteArray())
                descFos.close()
                Log.i("RenderActivity", "Description file: ${descFileName}, saved to ${descFile.absolutePath}")

                Toast.makeText(this, "SVG and description saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("RenderActivity", "Error saving SVG or description", e)
                Toast.makeText(this, "Failed to save SVG or description", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No SVG content to save", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteSvgFile() {
        if (currentFileName != null) {
            try {
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), currentFileName!!)
                val descFileName = currentFileName!!.replace(".svg", "_description.txt")
                val descFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName)

                if (file.exists()) {
                    file.delete()
                    Log.i("RenderActivity", "SVG file deleted: ${file.absolutePath}")
                }

                if (descFile.exists()) {
                    descFile.delete()
                    Log.i("RenderActivity", "Description file deleted: ${descFile.absolutePath}")
                }

                Toast.makeText(this, "SVG and description deleted", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                Log.e("RenderActivity", "Error deleting SVG or description", e)
                Toast.makeText(this, "Failed to delete SVG or description", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No SVG file to delete", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateFileName(format: String): String {
        val timestamp = System.currentTimeMillis()
        return if (format == "svg") {
            "rendered_latex_${timestamp}.${format}"
        } else if (format == "txt") {
            "rendered_latex_${timestamp}_description.${format}"
        } else {
            throw Exception("Invalid file format: $format")
        }
    }

    private class SVGDrawable(val svg: SVG, val scale: Int) : android.graphics.drawable.Drawable() {
        override fun draw(canvas: android.graphics.Canvas) {
            val width = bounds.width() / scale
            val height = bounds.height() / scale
            svg.documentWidth = width.toFloat()
            svg.documentHeight = height.toFloat()
            svg.renderToCanvas(canvas)
        }

        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
        override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
    }
}