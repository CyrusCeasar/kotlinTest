package cn.sz.cyrus.kotlintest

import android.app.Application
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.squareup.leakcanary.LeakCanary


/**
 * Created by 41264 on 05/21/17.
 */
class TestApplication : Application() {
    val TAG = TestApplication::class.simpleName

    object vals{
        val speechSynthManager = SpeechSynthManager()
        val wakeUpManager = WakeUpManager()
        val asrManager = AsrManager()
    }



    override fun onCreate() {
      /*  val isInSamplerProcess = BlockCanaryEx.isInSamplerProcess(this)
        if (!isInSamplerProcess) {
            BlockCanaryEx.install(Config(this))
        }*/
        super.onCreate()

     /*   if (!isInSamplerProcess) {
            //your code start here
        }*/
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        // Normal app init code...

        Logger.init(TAG)                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                            // default 0

        Logger.d("$TAG onCreate")

        vals.asrManager.init(this@TestApplication)
        Thread({
            vals.wakeUpManager.init(this@TestApplication)
           if ( vals.speechSynthManager.check((this@TestApplication)))
                vals.speechSynthManager.init(this@TestApplication)
        }).start()

    }


}