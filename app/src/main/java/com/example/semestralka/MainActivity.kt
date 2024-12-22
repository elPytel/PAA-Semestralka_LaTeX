package com.example.semestralka

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRender = findViewById<Button>(R.id.btnRender)
        val etLatex = findViewById<EditText>(R.id.etLatex)
        val btnSavedFormulas = findViewById<Button>(R.id.btnSavedFormulas)

        btnRender.setOnClickListener {
            val latexInput = etLatex.text.toString()
            Log.d("MainActivity", "Latex input: $latexInput")
            val intent = Intent(this, RenderActivity::class.java)
            intent.putExtra("latex", latexInput)
            startActivity(intent)
        }

        btnSavedFormulas.setOnClickListener {
            val intent = Intent(this, SavedFormulasActivity::class.java)
            startActivity(intent)
        }
    }
}