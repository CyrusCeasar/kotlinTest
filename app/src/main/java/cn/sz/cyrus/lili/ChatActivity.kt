package cn.sz.cyrus.lili


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
import android.speech.SpeechRecognizer
import android.widget.*
import com.transitionseverywhere.ChangeText
import com.transitionseverywhere.TransitionManager
import kotlin.experimental.and


/**
 * Created by 41264 on 05/24/17.
 */
class ChatActivity : BaseActivity() {

    var btn_send: Button? = null
    var et_content: EditText? = null
    var rv_contents: RecyclerView? = null

    var img_voice_input: ImageView? = null
    var img_keyboard: ImageView? = null
    var ll_voice_input_window: LinearLayout? = null
    var ll_keyboard_input_window: LinearLayout? = null
    var tv_chinese_voice_input: Button? = null
    var tv_eng_voice_input: Button? = null

    val chatMsgDao = TestApplication.appDataBase!!.getChatMsgDao()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chat)

//        aiDataService = AIDataService(this, config)
        btn_send = findViewById(R.id.btn_send) as Button
        et_content = findViewById(R.id.et_content) as EditText
        rv_contents = findViewById(R.id.rv_contents) as RecyclerView

        tv_eng_voice_input = findViewById(R.id.tv_eng_voice_input) as Button
        tv_chinese_voice_input = findViewById(R.id.tv_chinese_voice_input) as Button
        ll_keyboard_input_window = findViewById(R.id.ll_keyboard_input_window) as LinearLayout
        ll_voice_input_window = findViewById(R.id.ll_voice_input_window) as LinearLayout
        img_keyboard = findViewById(R.id.img_keyboard) as ImageView
        img_voice_input = findViewById(R.id.img_voice_input) as ImageView

        img_keyboard!!.setOnClickListener {
            ll_voice_input_window!!.visibility = View.INVISIBLE
            ll_keyboard_input_window!!.visibility = View.VISIBLE
        }
        img_voice_input!!.setOnClickListener {
            ll_voice_input_window!!.visibility = View.VISIBLE
            ll_keyboard_input_window!!.visibility = View.INVISIBLE
        }
        tv_eng_voice_input!!.setOnClickListener { TestApplication.asrManager.startAsrDialog(this, REQUEST_EN_UI, AsrManager.LANGULAGE_EN) }
        tv_chinese_voice_input!!.setOnClickListener { TestApplication.asrManager.startAsrDialog(this, REQUEST_CN_UI, AsrManager.LANGULAGE_CN) }

        /* turingManager = TuringManager(this, "4845d979e70d4f6bad9c790d342f8c02", "9ad19814c79a12a9")
         turingManager!!.setHttpRequestListener(object : HttpRequestListener {
             override fun onFail(p0: Int, p1: String?) {
                 toastError("result_code:$p0  result_msg:$p1")
             }

             override fun onSuccess(p0: String?) {
           //      p0?.let { contents.add(ChatItem(p0,ChatItem.TYPE.ROBOT)) }
                 rv_contents!!.adapter.notifyItemInserted(TestApplication.INSTANCE.getHistorySize())

                 Logger.d(p0)
             }

         })*/
        btn_send!!.setOnClickListener {
            if (TextUtils.isEmpty(et_content!!.text.toString())) {
                toastError("输入内容不能为空")
            } else {
                //         contents.add(ChatItem(et_content!!.text.toString(),ChatItem.TYPE.MASTER))

          /*      if (TestApplication.robotCenter!!.robotService.storeMsg(ChatMsg(et_content!!.text.toString(), ChatMsg.TO.ROBOT))) {
                    rv_contents!!.adapter.notifyItemInserted(TestApplication.INSTANCE.getHistorySize())
                    rv_contents!!.scrollToPosition(TestApplication.INSTANCE.getHistorySize())
                }
                TestApplication.robotCenter!!.robotService.chat(et_content!!.text.toString(), {
                    reponse ->
                    rv_contents!!.adapter.notifyItemInserted(TestApplication.INSTANCE.getHistorySize())
                    rv_contents!!.scrollToPosition(TestApplication.INSTANCE.getHistorySize())
                })*/
                val msg = et_content!!.text.toString()
                if(isChinese(msg)){
                    speakChinese(msg)
                }else{
                    TestApplication.robotCenter!!.robotService.storeMsg(ChatMsg(msg, ChatMsg.ROBOT))
                    TestApplication.robotCenter!!.robotService.chat(msg)
                }
                et_content!!.setText("")
            }
        }



        rv_contents!!.layoutManager = LinearLayoutManager(this)
        rv_contents!!.adapter = object : Adapter<ChatItemHolder>() {
            override fun onBindViewHolder(p0: ChatItemHolder?, p1: Int) {
                val chatMsg: ChatMsg = TestApplication.INSTANCE.getChatMsg(p1)
                p0?.tv_content?.text = chatMsg.msg

                p0?.tv_content?.setOnLongClickListener {
                    TestApplication.robotCenter!!.speak(chatMsg.msg)
                    true
                }
                p0?.tv_content?.setOnClickListener {
                    val transitionsContainer = p0?.tv_content?.parent
                    if (p0?.tv_content?.text.toString().equals(chatMsg.translMsg)) {
                        TransitionManager.beginDelayedTransition(transitionsContainer as ViewGroup?,
                                ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                        p0?.tv_content?.text = chatMsg.msg
                    } else {
                        if (TextUtils.isEmpty(chatMsg.translMsg)) {
                            TranslateService().translate(TranslateService.vals.EN, TranslateService.vals.CN, TestApplication.INSTANCE.getChatMsg(p1).msg, {
                                result ->
                                chatMsg.translMsg = result
                                chatMsgDao.updateChatMsg(chatMsg)
                                TransitionManager.beginDelayedTransition(transitionsContainer as ViewGroup?,
                                        ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                                p0?.tv_content?.text = result
                            })
                        } else {
                            TransitionManager.beginDelayedTransition(transitionsContainer as ViewGroup?,
                                    ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                            p0?.tv_content?.text = chatMsg.translMsg
                        }
                    }
                }
            }

            override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ChatItemHolder {
                return when (p1) {
                    ChatMsg.TO.ROBOT -> ChatItemHolder(layoutInflater.inflate(R.layout.item_chat_master, null))
                    ChatMsg.TO.MASTER -> ChatItemHolder(layoutInflater.inflate(R.layout.item_chat_robot, null))
                    else -> ChatItemHolder(layoutInflater.inflate(R.layout.item_chat, null))
                }
            }

            override fun getItemCount(): Int {
                return TestApplication.INSTANCE.getHistorySize()
            }

            override fun getItemViewType(position: Int): Int {
                return TestApplication.INSTANCE.getChatMsg(position).to
            }
        }
        TestApplication.robotCenter!!.robotService.getPrompt()

    }

    val msgChangeListener = {
        rv_contents!!.adapter.notifyItemInserted(TestApplication.INSTANCE.getHistorySize())
        rv_contents!!.scrollToPosition(rv_contents!!.adapter.itemCount - 1)
    }

    override fun onStart() {
        super.onStart()
        TestApplication.INSTANCE.addMsgChangeListener(msgChangeListener)
    }


    override fun onStop() {
        super.onStop()
        TestApplication.INSTANCE.removeMsgChangeListener(msgChangeListener)
    }

    override fun onResume() {
        super.onResume()
        rv_contents!!.scrollToPosition(rv_contents!!.adapter.itemCount - 1)

    }

    /* class ChatItem(content:String,type:Int){
         object TYPE{
             val MASTER = 0
             val ROBOT = 1
         }
         val content:String = content
         val type:Int = type
         var trsResult:String ?= null
     }*/

    class ChatItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tv_content: TextView? = itemView!!.findViewById(R.id.tv_content) as TextView
    }

    val REQUEST_EN_UI = 1

    val REQUEST_CN_UI = 2


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val results = data.getStringArrayListExtra(SpeechRecognizer.RESULTS_RECOGNITION)
            Logger.i(results.toString())
            if (results.size > 0) {
                var msg = results[0]
                if (msg.contains(",")) {
                    msg = msg.substring(0, msg.indexOf(","))
                }
                when (requestCode) {
                    REQUEST_EN_UI -> {
                        TestApplication.robotCenter!!.robotService.storeMsg(ChatMsg(msg, ChatMsg.ROBOT))
                        TestApplication.robotCenter!!.robotService.chat(msg)
                    }
                    REQUEST_CN_UI -> {
                        speakChinese(msg)
                    }
                }
                // data.get... TODO 识别结果包含的信息见本文档的“结果解析”一节
            }
        }
    }
    fun speakChinese(msg:String){
        TranslateService().translate(TranslateService.vals.CN, TranslateService.vals.EN,msg, {
            result ->
            var chatMsg = ChatMsg(result,ChatMsg.ROBOT)
            chatMsg.translMsg = msg
            TestApplication.robotCenter!!.robotService.storeMsg(chatMsg)
            TestApplication.robotCenter!!.robotService.chat(result)
        })
    }

    // 判断一个字符是否是中文
    fun isChinese(c: Char): Boolean {
        return c.toInt() >= 0x4E00 && c.toInt() <= 0x9FA5// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    fun isChinese(str: String?): Boolean {
        if (str == null)
            return false
        for (c in str.toCharArray()) {
            if (isChinese(c))
                return true// 有一个中文字符就返回
        }
        return false
    }
}