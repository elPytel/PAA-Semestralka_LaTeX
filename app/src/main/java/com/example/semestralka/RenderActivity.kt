package com.example.semestralka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.iffanmajid.katexmathview.KatexMathView
import kotlinx.android.synthetic.main.activity_render.*

class RenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render)

        val latexFormula = intent.getStringExtra("latex")

        katexMathView.render(latexFormula)
    }
}
