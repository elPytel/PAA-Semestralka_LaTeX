package com.example.semestralka

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class RenderActivity : AppCompatActivity() {
    private var isModified = false

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

        btnSave.setBackgroundColor(if (isModified) Color.GREEN else Color.GRAY)

        btnSave.setOnClickListener {
            if (isModified) {
                equation?.saveOrUpdateFile(this)
                isModified = false
                btnSave.setBackgroundColor(Color.GRAY)
            } else {
                Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            equation?.deleteFile(this)
        }

        
        val jsonFileName = intent.getStringExtra("jsonFileName")
        val latex = intent.getStringExtra("latex")

        if (jsonFileName != null) {
            equation = Equation(jsonFileName, this, imageView)
            etDescription.setText(equation.description)
            etLabel.setText(equation.label)
            etScale.setText(equation.scale.toString())
            equation.displaySvg()
        } else if (latex != null) {
            equation = Equation(this, imageView)
            equation.updateEquation(latex)
        }

        etScale.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newScale = s.toString().toIntOrNull() ?: 10
                equation?.updateScale(newScale)
                equation?.displaySvg()
                isModified = true
                btnSave.setBackgroundColor(Color.GREEN)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etLabel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                equation?.updateLabel(s.toString())
                isModified = true
                btnSave.setBackgroundColor(Color.GREEN)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                equation?.updateDescription(s.toString())
                isModified = true
                btnSave.setBackgroundColor(Color.GREEN)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}