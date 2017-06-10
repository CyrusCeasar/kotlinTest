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


class RebotService{

    fun chat(msg:String,callBack:(response:String)->Unit){
        "http://192.168.0.2:8080/testcontroller.do?getRequest&token=$msg".httpGet().responseString { request, response, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                    val error = result.getAs<FuelError>()
                    val jsonObj = JSONObject()
                    jsonObj.put("obj","i am sick")
                    callBack.invoke(jsonObj.toString())
                    Logger.e(error.toString())
                }
                is Result.Success -> {

                    val data = result.getAs<String>()
                    Logger.i(data)
                    callBack.invoke(data!!)

                }
            }
        }
    }
}