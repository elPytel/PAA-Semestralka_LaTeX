package com.example.semestralka

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRender = findViewById<Button>(R.id.btnRender)
        val etLatex = findViewById<EditText>(R.id.etLatex)
        val btnSavedFormulas = findViewById<Button>(R.id.btnSavedFormulas)
        val btnLoadDefaults = findViewById<Button>(R.id.btnLoadDefaults)

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

        btnLoadDefaults.setOnClickListener {
            loadDefaultCards()
        }
    }

    private fun loadDefaultCards() {
        val inputStream = assets.open("DefaultCards.json")
        val jsonContent = InputStreamReader(inputStream).use { it.readText() }
        inputStream.close()

        val type = object : TypeToken<List<EquationData>>() {}.type
        val defaultCards: List<EquationData> = Gson().fromJson(jsonContent, type)

        defaultCards.forEach { equationData ->
            val jsonFileName = equationData.thisFileName
            val jsonFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), jsonFileName)
            if (!jsonFile.exists()) {
                val equation = Equation(equationData, this, null)
                equation.updateEquation(equationData.equation)
                equation.saveToFile(this, equationData.svgFileName)
            }
        }
        Toast.makeText(this, "Default cards loaded", Toast.LENGTH_SHORT).show()
    }
}