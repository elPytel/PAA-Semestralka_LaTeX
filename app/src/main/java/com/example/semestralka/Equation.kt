package com.example.semestralka

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.caverock.androidsvg.SVG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Type

data class EquationData(
    val equation: String,
    val label: String,
    val description: String,
    val scale: Int,
    val svgFileName: String,
    val thisFileName: String,
)

class Equation {
    var equation: String? = null
    var description: String? = null
    var label: String? = null
    var svgFile: SVG? = null
    var scale: Int = 1

    private var imageView: ImageView? = null
    private var jsonFileName: String? = null
    var svgFileName: String? = null
    var context: Context

    constructor(context: Context, imageView: ImageView?) {
        this.context = context
        this.imageView = imageView
    }

    constructor(equationData: EquationData, context: Context, imageView: ImageView?) {
        this.context = context
        this.imageView = imageView
        data2equation(equationData)
        try {
            this.svgFile = loadSvgFromFile(equationData.svgFileName, context)
            Log.d("Equation", "SVG loaded from file")
        } catch (e: Exception) {
            Log.e("Equation", "Error loading SVG from file, will try to update equation from web.")
            updateEquation(equationData.equation)
        }
    }

    constructor(jsonFile: String, context: Context, imageView: ImageView?) {
        val equationData = loadFromJsonFile(context, jsonFile)
        this.context = context
        this.jsonFileName = jsonFile
        this.imageView = imageView
        data2equation(equationData!!)
        try {
            this.svgFile = loadSvgFromFile(equationData.svgFileName, context)
            Log.d("Equation", "SVG loaded from file")
        } catch (e: Exception) {
            Log.e("Equation", "Error loading SVG from file, will try to update equation from web.", e)
            updateEquation(equationData.equation)
        }
    }

    fun data2equation(data: EquationData) {
        this.equation = data.equation
        this.label = data.label
        this.description = data.description
        this.scale = data.scale
        this.jsonFileName = data.thisFileName
        this.svgFileName = data.svgFileName
    }

    fun equation2data(): EquationData {
        return EquationData(equation!!, label!!, description!!, scale, "${jsonFileName!!.replace(".json", ".svg")}", jsonFileName!!)
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

    fun updateEquation(equation: String) {
        this.equation = equation
        val url = "https://math.vercel.app/?from=$equation"
        Log.d("Equation", "Fetching URL: $url with scale: $scale")
        DownloadTask(context, this).execute(url)
    }

    fun updateLabel(label: String) {
        this.label = label
    }

    fun updateScale(scale: Int) {
        this.scale = scale
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun saveData2JsonFile(context: Context, fileName: String) {
        val jsonFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (!jsonFile.exists()) {
            jsonFileName = fileName
            val jsonContent = Gson().toJson(equation2data())
            val fos = FileOutputStream(jsonFile)
            fos.write(jsonContent.toByteArray())
            fos.close()
            Log.i("Equation", "JSON file: ${fileName}, saved to ${jsonFile.absolutePath}")
        } else {
            Log.i("Equation", "JSON file: ${fileName} already exists")
        }
    }

    private fun loadSvgFromFile(fileName: String, context: Context): SVG {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val inputStream = FileInputStream(file)
        val svgContent = inputStream.bufferedReader().use { it.readText() }
        val svg = SVG.getFromString(svgContent)
        inputStream.close()
        return svg
    }

    fun saveOrUpdateFile(context: Context) {
        if (equation != null) {
            if (jsonFileName != null) {
                deleteFile(context)
                saveToFile(context)
            } else {
                saveToFile(context)
            }
        } else {
            Toast.makeText(context, "No SVG content to save", Toast.LENGTH_LONG).show()
        }
    }

    fun saveSVG2File(context: Context, fileName: String) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (!file.exists()) {
            val fos = FileOutputStream(file)
            fos.write(equation!!.toByteArray())
            fos.close()
            Log.i("Equation", "SVG file: ${fileName}, saved to ${file.absolutePath}")
        } else {
            Log.i("Equation", "SVG file: ${fileName} already exists")
        }
    }

    private fun saveDescription2File(context: Context, descFileName: String) {
        val descFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), descFileName)
        if (!descFile.exists()) {
            val descFos = FileOutputStream(descFile)
            descFos.write(description!!.toByteArray())
            descFos.close()
            Log.i("Equation", "Description file: ${descFileName}, saved to ${descFile.absolutePath}")
        } else {
            Log.i("Equation", "Description file: ${descFileName} already exists")
        }
    }

    fun saveToFile(context: Context, fileName: String) {
        try {
            saveSVG2File(context, fileName)
            saveData2JsonFile(context, fileName.replace(".svg", ".json"))
        } catch (e: Exception) {
            Log.e("Equation", "Error saving SVG or Json", e)
            Toast.makeText(context, "Failed to save SVG or Json", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveToFile(context: Context) {
        val timestamp = System.currentTimeMillis()
        val fileName = "rendered_latex_${timestamp}.svg"

        saveToFile(context, fileName)
        Toast.makeText(context, "Card saved", Toast.LENGTH_LONG).show()
    }

    fun deleteFile(context: Context) {
        try {
            val jsonFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), jsonFileName!!)
            val equationData = loadFromJsonFile(context, jsonFileName!!)

            if (equationData != null) {
                val svgFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), equationData.svgFileName)

                if (svgFile.exists()) {
                    svgFile.delete()
                    Log.i("Equation", "SVG file deleted: ${svgFile.absolutePath}")
                }

                if (jsonFile.exists()) {
                    jsonFile.delete()
                    Log.i("Equation", "JSON file deleted: ${jsonFile.absolutePath}")
                }

                Toast.makeText(context, "SVG and JSON deleted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to load JSON data", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("Equation", "Error deleting SVG or JSON", e)
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

    companion object {
        fun loadFromJsonFile(context: Context, jsonFileName: String): EquationData? {
            return try {
                val jsonFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), jsonFileName)
                val jsonInputStream = FileInputStream(jsonFile)
                val jsonContent = jsonInputStream.bufferedReader().use { it.readText() }
                jsonInputStream.close()
                Gson().fromJson(jsonContent, EquationData::class.java)
            } catch (e: Exception) {
                Log.e("Equation", "Error loading JSON from file", e)
                null
            }
        }
    }
}

