package com.ivy.base.audio

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}
