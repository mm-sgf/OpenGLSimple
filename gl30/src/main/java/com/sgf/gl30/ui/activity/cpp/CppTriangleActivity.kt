package com.sgf.gl30.ui.activity.cpp

import android.os.Bundle
import com.sgf.gl30.databinding.ActivityTriangleBinding
import com.sgf.gl30.ui.main.BaseActivity

class CppTriangleActivity : BaseActivity() {

    private lateinit var binding: ActivityTriangleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriangleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}