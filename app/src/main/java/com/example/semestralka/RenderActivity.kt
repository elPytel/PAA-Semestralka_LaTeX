package com.example.semestralka

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import java.io.File
import java.io.FileInputStream

class RenderActivity : AppCompatActivity() {

    var svgContent: String? = null
    private var currentFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render)

        val etLabel = findViewById<EditText>(R.id.etLabel)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etScale = findViewById<EditText>(R.id.etScale)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnBack = findViewById<Button>(R.id.btnBack)
        var equation: Equation? = null

        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            equation?.saveToFile(this)
        }

        btnDelete.setOnClickListener {
            equation?.deleteFile(this)
        }

        // Get svg image and scale from intent
        val fileName = intent.getStringExtra("fileName")
        val scale = intent.getIntExtra("scale", 10)

        if (fileName != null) {
            currentFileName = fileName
            equation = Equation(fileName, fileName.replace(".svg", "_description.txt"), this, imageView)
            etDescription.setText(equation.description)
            equation.displaySvg()
        } else {
            val latex = intent.getStringExtra("latex")
            if (latex != null) {
                equation = Equation(latex, scale, imageView)
            }
        }
    }
}