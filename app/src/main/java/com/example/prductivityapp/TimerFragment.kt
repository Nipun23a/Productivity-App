package com.example.prductivityapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {
    private lateinit var timerDisplay : TextView
    private lateinit var startbutton : Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private var isRunning = false
    private var elapsedTime : Long = 0
    private  var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_timer,container,false)
        timerDisplay = view.findViewById(R.id.timer_display)
        startbutton = view.findViewById(R.id.start_timer)
        stopButton = view.findViewById(R.id.stop_timer)
        resetButton = view.findViewById(R.id.reset_timer)

        startbutton.setOnClickListener {startTimer()}
        stopButton.setOnClickListener {stopTimer()}
        resetButton.setOnClickListener{resetTimer()}

        return view
    }

    private fun startTimer(){
        if (!isRunning){
            isRunning = true
            handler.post(updateTimer)
        }
    }

    private fun stopTimer(){
        isRunning = false
        handler.removeCallbacks(updateTimer)
    }

    private fun resetTimer(){
        stopTimer()
        elapsedTime = 0
        updateDisplay()
    }

    private val updateTimer = object :Runnable{
        override fun run() {
            elapsedTime ++
            updateDisplay()
            if (isRunning){
                handler.postDelayed(this,1000)
            }
        }
    }

    private fun updateDisplay(){
        val hours = TimeUnit.SECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.SECONDS.toMinutes(elapsedTime) % 60
        val seconds = elapsedTime % 60
        timerDisplay.text = String.format("%02d:%02d:%02d",hours,minutes,seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimer)
    }
}