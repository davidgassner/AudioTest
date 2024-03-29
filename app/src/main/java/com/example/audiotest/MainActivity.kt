package com.example.audiotest

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiotest.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val AUDIO_FILE_NAME = "root-cellar-blues.wav"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cachedFilePath: String
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        copyAudioFileToCache()

        initEvents()
    }

    private fun initEvents() {
        binding.apply {
            prepAndPlayButton.setOnClickListener { prepareAndPlay() }
            prepareButton.setOnClickListener { preparePlayer() }
            playButton.setOnClickListener { play() }
            asyncButton.setOnClickListener { prepareAsyncAndPlay() }
            stopButton.setOnClickListener { stopPlayer() }
        }
    }

    private fun prepareAndPlay() {
        stopPlayer()
        player = MediaPlayer().also {
            it.setDataSource(cachedFilePath)
            it.prepare()
            it.start()
        }
    }

    private fun preparePlayer() {
        stopPlayer()
        player = MediaPlayer().also {
            it.setDataSource(cachedFilePath)
            it.prepare()
        }
        binding.playButton.isEnabled = true
        Toast.makeText(this, "Prepared", Toast.LENGTH_SHORT).show()
    }

    private fun play() {
        player?.start()
    }

    private fun prepareAsyncAndPlay() {
        stopPlayer()
        player = MediaPlayer().also {
            it.setDataSource(cachedFilePath)
            it.setOnPreparedListener { player ->
                player.start()
            }
            it.prepareAsync()
        }
    }

    private fun stopPlayer() {
        player?.let {
            it.stop()
            it.reset()
            it.release()
        }
        player = null
        binding.playButton.isEnabled = false
    }

    private fun copyAudioFileToCache() {
        val cachedAudioFile = File(cacheDir, AUDIO_FILE_NAME)
        try {
            assets.open(AUDIO_FILE_NAME).use { inputStream ->
                FileOutputStream(cachedAudioFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Toast.makeText(
                this,
                "File copied to: ${cachedAudioFile.absolutePath}", Toast.LENGTH_SHORT
            ).show()
            cachedFilePath = cachedAudioFile.absolutePath
        } catch (e: IOException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}