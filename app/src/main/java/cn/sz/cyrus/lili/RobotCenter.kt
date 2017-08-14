package cn.sz.cyrus.lili

import android.content.Context
import android.os.Looper
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.orhanobut.logger.Logger
import org.json.JSONObject
import java.util.*


/**
 * Created by chenlei2 on 2017/5/25 0025.
 */
class RobotCenter(context: Context) : EventListener {

    //    val turingManager: TuringManager = TuringManager(context, "4845d979e70d4f6bad9c790d342f8c02", "9ad19814c79a12a9")
    lateinit var eventManager: EventManager
    val params = HashMap<String, String>()
    lateinit var speechSynthManager: SpeechSynthManager
    //  val answerContents = arrayListOf("你好啊", "你好", "有什么可以帮您", "主人您好", "主人好")
    val answerContentsEn = arrayListOf("what can i do for you", "hello")
    val robotService: RobotService = RobotService()

    init {
        speechSynthManager = SpeechSynthManager()
        if (speechSynthManager.check((context)))
            speechSynthManager.init(context)
        eventManager = EventManagerFactory.create(context, "wp")
        eventManager.registerListener(this)
        // 3) 通知唤醒管理器, 启动唤醒功能
        params.put("kws-file", "assets:///haloulili.bin") // 设置唤醒资源, 唤醒资源文件可以放到任意可访问路径（同时支持放到assets目录，如实例中的写法）。唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出

        startWakeUp()
        /*   turingManager.setHttpRequestListener(object : HttpRequestListener {
               override fun onFail(p0: Int, p1: String?) {
                   Logger.w("$p0 $p1")
               }

               override fun onSuccess(p0: String?) {
                   val jsonObj = JSONObject(p0)
                   val code = jsonObj.getInt("code")
                   if (code == 100_000) {
                       val text = jsonObj.getString("text")
                       speechSynthManager.speak(text)
                   }
                   Logger.i(p0)
               }

           })*/

    }

    fun speak(content: String) {
        speechSynthManager.speak(content)
    }

    fun startWakeUp() {
        eventManager.send("wp.start", JSONObject(params).toString(), null, 0, 0)
    }

    fun stopWakeUp() {
        eventManager.send("wp.stop", null, null, 0, 0)
    }

    override fun onEvent(p0: String?, p1: String?, p2: ByteArray?, p3: Int, p4: Int) {
        Logger.v("$p0 $p1 $p3 $p4")
        val jsonobj = JSONObject(p1)

        if (jsonobj.has("word") && "哈喽莉莉".equals(jsonobj["word"])) {
            stopWakeUp()
            speechSynthManager.speak(answerContentsEn[Random().nextInt(answerContentsEn.size)], {
                android.os.Handler(Looper.getMainLooper()).post({
                    TestApplication.asrManager.startASR({
                        code, str ->
                        startWakeUp()
                        if (code == 1) {
                            robotService.storeMsg(ChatMsg(str, ChatMsg.ROBOT))
                            robotService.chat(str, {
                                response ->
                                val respObj = JSONObject(response)
                                var resp: String? = null
                                if (respObj.has("result_content")) {
                                    resp = respObj["result_content"] as String
                                } else {
                                    resp = "i am sick"
                                }
                                TestApplication.robotCenter?.speechSynthManager?.speak(resp)
                            })
                            //       turingManager.requestTuring(str)
                        }
                        Logger.i("$code $str")
                    })
                })

            })

        }


    }

}