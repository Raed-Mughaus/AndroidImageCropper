package com.raed.qas

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val qasView = findViewById<QasView>(R.id.qasView)
        qasView.bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)

        val rotationWheelView = findViewById<RotationWheelView>(R.id.rotationWheelView)
        rotationWheelView.onRotationChanged = {
            qasView.bitmapRotation = it
        }
    }

}
