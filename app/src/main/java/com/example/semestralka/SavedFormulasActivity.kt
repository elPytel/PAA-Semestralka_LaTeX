package com.example.semestralka

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.File

class SavedFormulasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_formulas)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val formulas = loadSavedFormulas()
        val adapter = FormulasAdapter(formulas) { equationData ->
            val intent = Intent(this, RenderActivity::class.java)
            intent.putExtra("jsonFileName", equationData.thisFileName)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun loadSavedFormulas(): List<EquationData> {
        val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val files = dir?.listFiles { _, name -> name.endsWith(".json") } ?: return emptyList()
        return files.mapNotNull { file ->
            Log.d("SavedFormulasActivity", "Loading file: ${file.name}")
            val equationData = Equation.loadFromJsonFile(this, file.name)
            Log.d("SavedFormulasActivity", "Loaded equation: ${equationData!!.label}")
            equationData
        }
    }
}
