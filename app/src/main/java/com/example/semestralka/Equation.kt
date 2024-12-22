package com.example.semestralka

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.caverock.androidsvg.SVG
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Equation {
    var equation: String? = null
    var svgFile: SVG? = null
    var description: String? = null
    private var imageView: ImageView? = null
    private var scale: Int = 1

    constructor(equation: String, scale: Int, imageView: ImageView) {
        this.equation = equation
        this.scale = scale
        this.imageView = imageView
        val url = "https://math.vercel.app/?from=$equation"
        Log.d("Equation", "Fetching URL: $url with scale: $scale")
        DownloadTask(this).execute(url)
    }

    constructor(svgFileName: String, descriptionFileName: String, context: Context, imageView: ImageView) {
        this.imageView = imageView
        this.svgFile = loadSvgFromFile(svgFileName, context)
        this.description = loadDescriptionFromFile(descriptionFileName, context)
    }

    fun setSvgContent(svgContent: String) {
        this.equation = svgContent
        this.svgFile = SVG.getFromString(svgContent)
    }

    fun displaySvg() {
        if (svgFile != null && imageView != null) {
            imageView!!.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
            imageView!!.setImageDrawable(SVGDrawable(svgFile!!, scale))
        }
    }

    // rename method to avoid clash
    fun updateDescription(description: String) {
        this.description = description
    }

    private fun loadSvgFromFile(fileName: String, context: Context): SVG {
        return try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val inputStream = FileInputStream(file)
            val svgContent = inputStream.bufferedReader().use { it.readText() }
            val svg = SVG.getFromString(svgContent)
            inputStream.close()
            Log.d("Equation", "SVG loaded from file")
            svg
        } catch (e: Exception) {
            Log.e("Equation", "Error loading SVG from file", e)
            Toast.makeText(context, "Failed to load SVG", Toast.LENGTH_LONG).show()
            SVG.getFromString("")
        }
    }

    private fun loadDescriptionFromFile(fileName: String, context: Context): String {
        return try {
            val descFileName = fileName.replace(".svg", "_description.txt")
            val descFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName)
            val descInputStream = FileInputStream(descFile)
            val description = descInputStream.bufferedReader().use { it.readText() }
            descInputStream.close()
            Log.d("Equation", "Description loaded from file")
            description
        } catch (e: Exception) {
            Log.e("Equation", "Error loading description from file", e)
            ""
        }
    }

    fun saveToFile(context: Context) {
        if (equation != null) {
            try {
                val fileName = generateFileName()
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName + ".svg")
                val fos = FileOutputStream(file)
                fos.write(equation!!.toByteArray())
                fos.close()
                Log.i("Equation", "SVG file: ${fileName}, saved to ${file.absolutePath}")

                val descFileName = fileName + "_description"
                val descFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName + ".txt")
                val descFos = FileOutputStream(descFile)
                descFos.write(description!!.toByteArray())
                descFos.close()
                Log.i("Equation", "Description file: ${descFileName}, saved to ${descFile.absolutePath}")

                Toast.makeText(context, "SVG and description saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("Equation", "Error saving SVG or description", e)
                Toast.makeText(context, "Failed to save SVG or description", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "No SVG content to save", Toast.LENGTH_LONG).show()
        }
    }

    fun deleteFile(context: Context) {
        if (svgFile != null) {
            try {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), svgFile!!.documentTitle)
                val descFileName = svgFile!!.documentTitle.replace(".svg", "_description.txt")
                val descFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName)

                if (file.exists()) {
                    file.delete()
                    Log.i("Equation", "SVG file deleted: ${file.absolutePath}")
                }

                if (descFile.exists()) {
                    descFile.delete()
                    Log.i("Equation", "Description file deleted: ${descFile.absolutePath}")
                }

                Toast.makeText(context, "SVG and description deleted", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("Equation", "Error deleting SVG or description", e)
                Toast.makeText(context, "Failed to delete SVG or description", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "No SVG file to delete", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateFileName(): String {
        val timestamp = System.currentTimeMillis()
        return "rendered_latex_${timestamp}"
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

