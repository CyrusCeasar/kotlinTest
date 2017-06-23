package cn.sz.cyrus.wemz


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.orhanobut.logger.Logger
import com.turing.androidsdk.HttpRequestListener
import com.turing.androidsdk.TuringManager
import org.json.JSONObject


/**
 * Created by 41264 on 05/24/17.
 */
class ChatActivity : BaseActivity() {

    var btn_send: Button? = null
    var et_content: EditText? = null
    var rv_contents: RecyclerView? = null
    var turingManager: TuringManager? = null
    val contents: ArrayList<ChatItem> = ArrayList()


    /* val config = AIConfiguration("0bdbbce8888c4af0ae33ddf448fc2108",ai.api.AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System
             )

     var aiDataService:AIDataService? = null*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chat)

//        aiDataService = AIDataService(this, config)
        btn_send = findViewById(R.id.btn_send) as Button
        et_content = findViewById(R.id.et_content) as EditText
        rv_contents = findViewById(R.id.rv_contents) as RecyclerView
        turingManager = TuringManager(this, "4845d979e70d4f6bad9c790d342f8c02", "9ad19814c79a12a9")
        turingManager!!.setHttpRequestListener(object : HttpRequestListener {
            override fun onFail(p0: Int, p1: String?) {
                toastError("result_code:$p0  result_msg:$p1")
            }

            override fun onSuccess(p0: String?) {
                p0?.let { contents.add(ChatItem(p0,ChatItem.TYPE.ROBOT)) }
                rv_contents!!.adapter.notifyItemInserted(contents.size)

                Logger.d(p0)
            }

        })
        btn_send!!.setOnClickListener {
            if (TextUtils.isEmpty(et_content!!.text.toString())) {
                toastError("输入内容不能为空")
            } else {
                contents.add(ChatItem(et_content!!.text.toString(),ChatItem.TYPE.MASTER))
                rv_contents!!.adapter.notifyItemInserted(contents.size)
                val rebotService = RebotService()
                rebotService.chat(et_content!!.text.toString(), {
                    reponse ->
                    val respObj = JSONObject(reponse)
                    var resp:String? = null;
                    if(respObj.has("result_content")){
                         resp = respObj["result_content"] as String
                    }else{
                        resp = "i am sick"
                    }
                    contents.add(ChatItem(resp,ChatItem.TYPE.ROBOT))
                    rv_contents!!.adapter.notifyItemInserted(contents.size)
                    TestApplication.vals.robotCenter?.speechSynthManager?.speak(resp)
                })
//                turingManager!!.requestTuring(et_content!!.text.toString())
                /*   val aiRequest = AIRequest()
                   aiRequest.setQuery(et_content!!.text.toString())
                   object : AsyncTask<AIRequest, Void, AIResponse>() {
                       override fun doInBackground(vararg requests: AIRequest): AIResponse? {
                           val request = requests[0]
                           try {
                               val response = aiDataService!!.request(aiRequest)
                               return response
                           } catch (e: AIServiceException) {
                           }

                           return null
                       }

                       override fun onPostExecute(aiResponse: AIResponse?) {
                           if (aiResponse != null) {
                               val result = aiResponse.result
                               contents.add(result.getResolvedQuery())
                               rv_contents!!.adapter.notifyItemInserted(contents.size)

                               Logger.d(result.getResolvedQuery())
                           }
                       }
                   }.execute(aiRequest)*/
                et_content!!.setText("")
            }
        }

        rv_contents!!.layoutManager = LinearLayoutManager(this)
        rv_contents!!.adapter = object : Adapter<ChatItemHolder>() {
            override fun onBindViewHolder(p0: ChatItemHolder?, p1: Int) {
                val chatitem:ChatItem = contents[p1]
                p0?.tv_content?.text = chatitem.trsResult?:chatitem.content
                p0?.tv_content?.setOnClickListener {
                    TranslateService().translate(TranslateService.vals.EN,TranslateService.vals.CN,contents[p1].content,{
                        result->
                        chatitem.trsResult = result
                        p0?.tv_content?.text = result
                    })
                 }
             }

            override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ChatItemHolder {
                when(getItemViewType(p1)){
                    ChatItem.TYPE.MASTER  ->  return ChatItemHolder(layoutInflater.inflate(R.layout.item_chat_master, null))
                    ChatItem.TYPE.ROBOT ->    return ChatItemHolder(layoutInflater.inflate(R.layout.item_chat_robot, null))
                    else ->  return ChatItemHolder(layoutInflater.inflate(R.layout.item_chat, null))
                }
            }

            override fun getItemCount(): Int {
                return contents.size
            }

            override fun getItemViewType(position: Int): Int {
                return contents[position].type
            }
        }
    }

    class ChatItem(content:String,type:Int){
        object TYPE{
            val MASTER = 0
            val ROBOT = 1
        }
        val content:String = content
        val type:Int = type
        var trsResult:String ?= null
    }

    class ChatItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tv_content: TextView? = itemView!!.findViewById(R.id.tv_content) as TextView
    }
}