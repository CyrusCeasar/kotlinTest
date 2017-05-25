package cn.sz.cyrus.kotlintest

import android.content.Context
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.orhanobut.logger.Logger
import org.json.JSONObject



/**
 * Created by chenlei2 on 2017/5/25 0025.
 */
class WakeUpManager : EventListener{


    var eventManager:EventManager ?= null
    val params = HashMap<String,String>()
    fun init(context:Context){
        eventManager = EventManagerFactory.create(context,"wp")
        eventManager?.registerListener(this)
        // 3) 通知唤醒管理器, 启动唤醒功能
        params.put("kws-file", "assets:///WakeUp.bin") // 设置唤醒资源, 唤醒资源文件可以放到任意可访问路径（同时支持放到assets目录，如实例中的写法）。唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        startWakeUp()
    }

    fun startWakeUp(){
        eventManager?.send("wp.start", JSONObject(params).toString(), null, 0, 0)
    }
    fun stopWakeUp(){
        eventManager?.send("wp.stop", null, null, 0, 0)
    }

    override fun onEvent(p0: String?, p1: String?, p2: ByteArray?, p3: Int, p4: Int) {
        Logger.d("$p0 $p1 $p3 $p4")
        val jsonobj = JSONObject(p1)
        if(jsonobj.has("word") && "小丁你好".equals(jsonobj["word"])){
            stopWakeUp()
            TestApplication.vals.asrManager.startASR({
                code,str->Logger.i("$code $str")
                startWakeUp()
            })
        }



    }

}