package cn.sz.cyrus.wemz

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.baidu.speech.VoiceRecognitionService
import com.orhanobut.logger.Logger
import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject




/**
 * Created by chenlei2 on 2017/5/25 0025.
 */
class AsrManager : RecognitionListener {

    var speechRecognizer: SpeechRecognizer? = null

    var callback:((resultCode:Int,str:String)->Unit) ?= null

    val ASR_BASE_FILE_PATH= "${Environment.getExternalStorageDirectory().path}/zhiyou/s_1"

    fun init(context: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, ComponentName(context, VoiceRecognitionService::class.java))
        speechRecognizer?.setRecognitionListener(this)
    }
    fun check():Boolean{

        return true
    }

    fun startASR(callback:((resultCode:Int,str:String)->Unit) ) {
        this.callback = callback
        val intent = Intent()
        bindParams(intent)
        speechRecognizer?.startListening(intent)
    }

    fun bindParams(intent: Intent) {
        intent.putExtra("sample", 16000) // 离线仅支持16000采样率
//        intent.putExtra("language", "cmn-Hans-CN") // 离线仅支持中文普通话
        intent.putExtra("language","en-GB")
        intent.putExtra("nlu","enable")
        val prop = 20000
        intent.putExtra("prop", 20000) // 输入
//    intent.putExtra("prop", 10060); // 地图
//    intent.putExtra("prop", 10001); // 音乐
//    intent.putExtra("prop", 10003); // 应用
//    intent.putExtra("prop", 10008); // 电话
//    intent.putExtra("prop", 100014); // 联系人
//    intent.putExtra("prop", 100016); // 手机设置
//    intent.putExtra("prop", 100018); // 电视指令
//    intent.putExtra("prop", 100019); // 播放器指令
//    intent.putExtra("prop", 100020); // 收音机指令
//    intent.putExtra("prop", 100021); // 命令词
        // value替换为资源文件实际路径
        intent.putExtra("asr-base-file-path", "/path/to/s_1")
        // value替换为license文件实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数
       // intent.putExtra("license-file-path", "/path/to/license-tmp-20150530.txt")
        if (prop == 10060) {
            // 地图类附加资源，value替换为资源文件实际路径
            intent.putExtra("lm-res-file-path", "/path/to/s_2_Navi")
        } else if (prop == 20000) {
            // 语音输入附加资源，value替换为资源文件实际路径
       //     intent.putExtra("lm-res-file-path", "/path/to/s_2_InputMethod")
        }
        val slotData = JSONObject()
        val name = JSONArray().put("张三").put("李四")
        val song = JSONArray().put("七里香").put("冰雨")
        val artist = JSONArray().put("周杰伦").put("刘德华")
        val app = JSONArray().put("手机百度").put("百度地图")
        val usercommand = JSONArray().put("关灯").put("开门")
        try {
            slotData.put("name", name)
            slotData.put("song", song)
            slotData.put("artist", artist)
            slotData.put("app", app)
            slotData.put("usercommand", usercommand)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        intent.putExtra("slot-data", slotData.toString())
    }

    fun startAsrDialog(activity: BaseActivity,requestCode: Int){
        val intent =  Intent("com.baidu.action.RECOGNIZE_SPEECH")
        bindParams(intent)
        activity.startActivityForResult(intent,requestCode)
    }


    override fun onReadyForSpeech(params: Bundle?) {
        Logger.d("readyForSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
       // Logger.d(rmsdB)
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Logger.d("onBufferReceived")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Logger.d("onPartialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Logger.d(eventType)
    }


    override fun onBeginningOfSpeech() {
        Logger.d("begin speech")
    }

    override fun onEndOfSpeech() {
        Logger.d("end speech")
    }

    override fun onError(error: Int) {
        Logger.d(error)
        callback?.invoke(0,"test")
        callback = null
    }

    override fun onResults(results: Bundle?) {
        Logger.d(results)
        val error = results?.getInt("error")
        if(error == 0){
            val results_recognition = results.getSerializable("results_recognition") as ArrayList<String>
            callback?.invoke(1,results_recognition[0])
        }
        callback?.invoke(0,"test")

        callback = null
    }

}