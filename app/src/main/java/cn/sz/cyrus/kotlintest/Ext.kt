package cn.sz.cyrus.kotlintest

import android.widget.Toast
import es.dmoral.toasty.Toasty

/**
 * Created by 41264 on 05/23/17.
 */

fun BaseActivity.toastInfo(msg:CharSequence,duration:Int = Toast.LENGTH_SHORT){
    Toasty.info(applicationContext,msg,duration,true).show()
}
fun BaseActivity.toastError(msg:CharSequence,duration:Int = Toast.LENGTH_SHORT){
    Toasty.error(applicationContext,msg,duration,true).show()
}
fun BaseActivity.toastSuccess(msg:CharSequence,duration:Int = Toast.LENGTH_SHORT){
    Toasty.success(applicationContext,msg,duration,true).show()
}