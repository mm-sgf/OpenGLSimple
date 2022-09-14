package com.sgf.opengl20.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgf.opengl20.CppOpenGLHandler

class CppPageViewModel : ViewModel() {

    private val cppOpenGLHandler = CppOpenGLHandler()
    val text = MutableLiveData("CPP Open GL Simple page :")

    fun loadCppString() {
        text.value = cppOpenGLHandler.getStringForJni();
    }

}