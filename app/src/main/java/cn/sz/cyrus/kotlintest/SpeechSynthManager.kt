package cn.sz.cyrus.kotlintest

import android.content.Context
import android.os.Environment
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.orhanobut.logger.Logger
import java.io.File


/**
 * Created by 41264 on 05/24/17.
 */
class SpeechSynthManager : SpeechSynthesizerListener {

    val mSpeechSynthesizer = SpeechSynthesizer.getInstance()

    val TXT_FILE_PATH = "${Environment.getExternalStorageDirectory().path}/zhiyou/bd_etts_text.dat"

    val SPEECH_FILE_PATH = "${Environment.getExternalStorageDirectory().path}/zhiyou/bd_etts_speech_female.dat"
    var callback: (() -> Unit)? = null
    fun speak(msg: String, onFinished: (() -> Unit)? = null) {
        callback = onFinished
        mSpeechSynthesizer.speak(msg)
    }

    fun check(context: Context): Boolean {
        val txtFile = File(TXT_FILE_PATH)
        val speechFile = File(SPEECH_FILE_PATH)
        if (txtFile.exists() && speechFile.exists()) {
            return true
        } else {
            if (!txtFile.parentFile.exists()) {
                txtFile.parentFile.mkdirs()
            }
            if (!txtFile.exists()) {
                txtFile.createNewFile()
                context.assets.open("data/bd_etts_text.dat").copyTo(txtFile.outputStream())
            }
            if (!speechFile.exists()) {
                speechFile.createNewFile()
                context.assets.open("data/bd_etts_speech_female.dat").copyTo(speechFile.outputStream())
            }
            return true
        }

    }

    // 初始化语音合成客户端并启动
    fun init(context: Context) {
        // 获取语音合成对象实例

        // 设置context
        mSpeechSynthesizer.setContext(context)
        // 设置语音合成状态监听器
        mSpeechSynthesizer.setSpeechSynthesizerListener(this)
        // 设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
        mSpeechSynthesizer.setApiKey("veE64XDN8KYCW8OY0nUW0V4H", "794f3ebb734216de3ed431e9b5d74d41")
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId("9688537")
        // 设置语音合成文本模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TXT_FILE_PATH)
        // 设置语音合成声音模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, SPEECH_FILE_PATH)
        // 设置语音合成声音授权文件
        /*   mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, "your_licence_path")
           // 获取语音合成授权信息*/
        val authInfo = mSpeechSynthesizer.auth(TtsMode.MIX)
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        if (authInfo.isSuccess) {
            mSpeechSynthesizer.initTts(TtsMode.MIX)
            Logger.i("tts init success");
            //mSpeechSynthesizer.speak("百度语音合成示例程序正在运行")
        } else {
            // 授权失败

        }
    }

    override fun onSynthesizeStart(p0: String?) {
        Logger.d(p0)
    }

    override fun onSpeechFinish(p0: String?) {

        Logger.d(p0)
        callback?.invoke()
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
   //       Logger.d("$p0   $p1")
    }

    override fun onSynthesizeFinish(p0: String?) {

        Logger.d(p0)

    }

    override fun onSpeechStart(p0: String?) {
        Logger.d(p0)
    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
        //     Logger.d("$p0   $p1  $p2")
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Logger.e("$p0  $p1")
    }

}