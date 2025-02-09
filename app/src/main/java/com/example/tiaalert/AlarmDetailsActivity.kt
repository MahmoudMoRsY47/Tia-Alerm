package com.example.tiaalert

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tiaalert.main.AlarmRing
import com.example.tiaalert.main.model.AlarmsModel
import com.example.tiaalert.main.utils.fromJson

class AlarmDetailsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_details)

//        val motionLayout: MotionLayout = findViewById(R.id.motionLayout)


        val alarmJson = intent.getStringExtra("alarm")
        val alarm = alarmJson?.fromJson<AlarmsModel>()

        findViewById<TextView>(R.id.tv_alarm_name).text =
            alarm?.name ?: getString(R.string.without_name)


//        // زر إيقاف النغمة
//        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
//            override fun onTransitionStarted(
//                motionLayout: MotionLayout?,
//                startId: Int,
//                endId: Int
//            ) {
//            }
//
//            override fun onTransitionChange(
//                motionLayout: MotionLayout?,
//                startId: Int,
//                endId: Int,
//                progress: Float
//            ) {
//            }
//
//            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
//                if (currentId == R.id.end) {
//                    AlarmRing.stop()
//                    finish() // إغلاق الشاشة بعد السحب
//                }
//            }
//
//            override fun onTransitionTrigger(
//                motionLayout: MotionLayout?,
//                triggerId: Int,
//                positive: Boolean,
//                progress: Float
//            ) {
//            }
//        })

        findViewById<Button>(R.id.stopAlarmButton).setOnClickListener {
            AlarmRing.stop()
            finish() // إغلاق الشاشة بعد السحب
        }

    }


}