package cn.sz.cyrus.wemz

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import com.orhanobut.logger.Logger
import org.json.JSONObject

/**
 * Created by chenlei2 on 2017/6/8 0008.
 */


class RobotService {

    fun chat(msg:String){
        "http://39.108.179.107:8080/lili/test?req_msg=$msg".httpGet().responseString { request, response, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                    val error = result.getAs<FuelError>()
                    val jsonObj = JSONObject()
                    jsonObj.put("obj","i am sick")

                    storeMsg(ChatMsg("i am sick",ChatMsg.TO.MASTER))
                    Logger.e(error.toString())
                }
                is Result.Success -> {
                    val data = result.getAs<String>()
                    Logger.i(data)
                    val respObj = JSONObject(data)
                    var resp:String? = null;
                    if(respObj.has("result_content")){
                        resp = respObj["result_content"] as String
                    }else{
                        resp = "i am sick"
                    }
                    storeMsg(ChatMsg(resp,ChatMsg.TO.MASTER))
                }
            }
        }
    }

    fun chat(msg:String,callBack:(response:String)->Unit){

        "http://39.108.179.107:8080/lili/test?req_msg=$msg".httpGet().responseString { request, response, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                    val error = result.getAs<FuelError>()
                    val jsonObj = JSONObject()
                    jsonObj.put("obj","i am sick")
                    callBack.invoke(jsonObj.toString())
                    storeMsg(ChatMsg("i am sick",ChatMsg.TO.MASTER))
                    Logger.e(error.toString())
                }
                is Result.Success -> {
                    val data = result.getAs<String>()
                    Logger.i(data)
                    val respObj = JSONObject(data)
                    var resp:String? = null;
                    if(respObj.has("result_content")){
                        resp = respObj["result_content"] as String
                    }else{
                        resp = "i am sick"
                    }
                    storeMsg(ChatMsg(resp,ChatMsg.TO.MASTER))
                    callBack.invoke(data!!)

                }
            }
        }
    }


    fun storeMsg(chatMsg:ChatMsg):Boolean{
        val chatMsgDao = TestApplication.appDataBase!!.getChatMsgDao()
        val result = chatMsgDao.insertChatMsg(chatMsg)
        if(result> 0){
            chatMsg.id = result.toInt()
            TestApplication.INSTANCE.addChatMsg(chatMsg)
            return true
        }
        return  false
    }
}