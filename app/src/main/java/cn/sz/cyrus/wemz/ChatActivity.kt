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



/**
 * Created by 41264 on 05/24/17.
 */
class ChatActivity : BaseActivity() {
    var btn_send: Button? = null
    var et_content: EditText? = null
    var rv_contents: RecyclerView? = null
    var turingManager: TuringManager? = null
    var contents: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chat)


        btn_send = findViewById(R.id.btn_send) as Button
        et_content = findViewById(R.id.et_content) as EditText
        rv_contents = findViewById(R.id.rv_contents) as RecyclerView
        turingManager = TuringManager(this, "4845d979e70d4f6bad9c790d342f8c02", "9ad19814c79a12a9")
        turingManager!!.setHttpRequestListener(object : HttpRequestListener {
            override fun onFail(p0: Int, p1: String?) {
                toastError("result_code:$p0  result_msg:$p1")
            }

            override fun onSuccess(p0: String?) {
                p0?.let { contents.add(p0) }
                rv_contents!!.adapter.notifyItemInserted(contents.size)

                Logger.d(p0)
            }

        })
        btn_send!!.setOnClickListener {
            if (TextUtils.isEmpty(et_content!!.text.toString())) {
                toastError("输入内容不能为空")
            } else {
                turingManager!!.requestTuring(et_content!!.text.toString())
                et_content!!.setText("")
            }
        }

        rv_contents!!.layoutManager = LinearLayoutManager(this)
        rv_contents!!.adapter = object : Adapter<ChatItemHolder>() {
            override fun onBindViewHolder(p0: ChatItemHolder?, p1: Int) {
                p0?.tv_content?.text = contents[p1]
            }

            override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ChatItemHolder {
                return ChatItemHolder(layoutInflater.inflate(R.layout.item_chat, null))
            }

            override fun getItemCount(): Int {
                return contents.size
            }

        }
    }

    class ChatItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tv_content: TextView ? = itemView!!.findViewById(R.id.tv_content) as TextView
    }
}