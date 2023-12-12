package com.example.semestralka

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRender = findViewById<Button>(R.id.btnRender)
        val etLatex = findViewById<EditText>(R.id.etLatex)

        btnRender.setOnClickListener {
            val latexInput = etLatex.text.toString()
            val intent = Intent(this, RenderActivity::class.java)
            intent.putExtra("latex", latexInput)
            startActivity(intent)
        }
    }
}