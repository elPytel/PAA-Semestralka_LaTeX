package com.example.semestralka

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class SavedFormulasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_formulas)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val formulas = loadSavedFormulas()
        val adapter = FormulasAdapter(formulas) { fileName ->
            val intent = Intent(this, RenderActivity::class.java)
            intent.putExtra("fileName", fileName)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun loadSavedFormulas(): List<String> {
        val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val files = dir?.listFiles { _, name -> name.endsWith(".svg") } ?: return emptyList()
        return files.map { it.name }
    }
}
