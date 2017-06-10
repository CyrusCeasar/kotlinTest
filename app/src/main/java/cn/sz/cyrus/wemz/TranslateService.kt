package cn.sz.cyrus.wemz

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import com.orhanobut.logger.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * Created by 41264 on 06/07/17.
 */
class TranslateService {
    val APP_KEY = "6167f7f681bc4ae7"
    val APP_SECRET = "rjpPbZkdjlChQB7qPJlVOrIEtxM4gGBa"

    object vals{
        val EN = "en"
        val CN = "zh_CHS"
    }

    fun translate(from:String,to:String,content:String,callBack:((result:String)->Unit)){
        val salt = System.currentTimeMillis().toString()
        val sign = md5(APP_KEY + content + salt + APP_SECRET)
        val params = HashMap<String, String>()
        params.put("q", content)
        params.put("from", from)
        params.put("to", to)
        params.put("sign", sign)
        params.put("salt", salt)
        params.put("appKey", APP_KEY)
        getUrlWithQueryString("http://openapi.youdao.com/api",params).httpGet().responseString { request, response, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                   val error = result.getAs<FuelError>()
                    Logger.e(error.toString())
                }
                is Result.Success -> {
                    val data = result.getAs<String>()
                    val jsonObj = JSONObject(data)
                    val translation = jsonObj["translation"] as JSONArray
                    val result = translation[0] as String
                    callBack.invoke(result!!)
                    Logger.i(result)
                }
            }
        }
    }

    /**
     * 生成32位MD5摘要
     * @param string
     * *
     * @return
     */
    fun md5(string: String): String{
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val btInput = string.toByteArray()
        try {
            /** 获得MD5摘要算法的 MessageDigest 对象  */
            val mdInst = MessageDigest.getInstance("MD5")
            /** 使用指定的字节更新摘要  */
            mdInst.update(btInput)
            /** 获得密文  */
            val md = mdInst.digest()
            /** 把密文转换成十六进制的字符串形式  */
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0

            for (byte0 in md) {
                str[k++] = hexDigits[ byte0.toInt().ushr(4)  and  0xf]
                str[k++] = hexDigits[(byte0 and 0xf).toInt()]
            }
            return String(str)
        } catch (e: NoSuchAlgorithmException) {
            return ""
        }

    }

    /**
     * 根据api地址和参数生成请求URL
     * @param url
     * *
     * @param params
     * *
     * @return
     */
    fun getUrlWithQueryString(url: String, params: Map<String, String>?): String {
        if (params == null) {
            return url
        }

        val builder = StringBuilder(url)
        if (url.contains("?")) {
            builder.append("&")
        } else {
            builder.append("?")
        }

        var i = 0
        for (key in params.keys) {
            val value = params[key] ?: // 过滤空的key
                    continue

            if (i != 0) {
                builder.append('&')
            }

            builder.append(key)
            builder.append('=')
            builder.append(encode(value))

            i++
        }

        return builder.toString()
    }

    /**
     * 进行URL编码
     * @param input
     * *
     * @return
     */
    fun encode(input: String?): String {
        if (input == null) {
            return ""
        }

        try {
            return URLEncoder.encode(input, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return input
    }
}