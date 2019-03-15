package com.death.rxnet

import android.app.Application
import io.github.kbiakov.codeview.classifier.CodeProcessor

class ApplicationController:Application(){
    override fun onCreate() {
        super.onCreate()
        CodeProcessor.init(this)
    }
}