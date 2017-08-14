package cn.sz.cyrus.lili

import android.app.Application
import android.content.Context
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.squareup.leakcanary.LeakCanary
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Created by 41264 on 05/21/17.
 */
class TestApplication : Application() {
    val TAG = TestApplication::class.simpleName

    companion object {
        var robotCenter: RobotCenter? = null
        var appDataBase: AppDatabase? = null
        val asrManager = AsrManager()
        private val msgHistory = ArrayList<ChatMsg>()
        private val onMsgChangeListenerList = CopyOnWriteArrayList<() -> Unit>()
        lateinit var INSTANCE: TestApplication
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }


    fun addChatMsg(chatMsg: ChatMsg) {
        msgHistory.add(chatMsg)
        onMsgChangeListenerList.forEach {
            listener ->
            listener.invoke()
        }
    }

    fun addMsgChangeListener(listener: () -> Unit) {
        onMsgChangeListenerList.add(listener)
    }

    fun removeMsgChangeListener(listener: () -> Unit) {
        onMsgChangeListenerList.remove(listener)
    }


    fun getHistorySize(): Int {
        return msgHistory.size
    }

    fun getChatMsg(position: Int): ChatMsg {
        return msgHistory[position]
    }


    override fun onCreate() {
        /*  val isInSamplerProcess = BlockCanaryEx.isInSamplerProcess(this)
          if (!isInSamplerProcess) {
              BlockCanaryEx.install(Config(this))
          }*/
        super.onCreate()
        INSTANCE = this

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


        appDataBase = AppDatabase.getInMemoryDatabase(this@TestApplication)
        val historys = appDataBase!!.getChatMsgDao().getAll()
        msgHistory.addAll(historys)
        if (msgHistory.isEmpty()) {
            val robotService = RobotService()
            robotService.storeMsg(ChatMsg("Hello,I am lili . Nice to meet you! Click this message Or long click. You will be surprised . If you tired of sending message to me , say 'Hello,LiLi' to me .", ChatMsg.TO.MASTER))
        }
        asrManager.init(this@TestApplication)
        robotCenter = RobotCenter(this@TestApplication)

    }


}