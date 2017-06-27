package cn.sz.cyrus.wemz

import android.app.Application
import android.content.Context
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.squareup.leakcanary.LeakCanary


/**
 * Created by 41264 on 05/21/17.
 */
class TestApplication : Application() {
    val TAG = TestApplication::class.simpleName

    object vals{
        var robotCenter:RobotCenter ?= null
        var appDataBase:AppDatabase ?= null
        val asrManager = AsrManager()
        val msgHistory = ArrayList<ChatMsg>()
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
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
        vals.appDataBase = AppDatabase.getInMemoryDatabase(this@TestApplication)
        val historys = vals.appDataBase!!.getChatMsgDao().getAll()
        vals.msgHistory.addAll(historys)
        if(vals.msgHistory.isEmpty()){
            val robotService  = RobotService()
            robotService.storeMsg(ChatMsg("Hello,I am lili . Nice to meet you! Click this msg Or long click. You will be surprised",ChatMsg.TO.MASTER))
        }
        Thread({
            vals.robotCenter = RobotCenter(this@TestApplication)
        }).start()
    }


}