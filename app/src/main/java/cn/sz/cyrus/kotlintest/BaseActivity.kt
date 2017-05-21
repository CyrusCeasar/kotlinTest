package cn.sz.cyrus.kotlintest

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.orhanobut.logger.Logger

/**
 * Created by 41264 on 05/21/17.
 */
open class BaseActivity :AppCompatActivity(), View.OnClickListener {

    val TAG = BaseActivity::class.simpleName

    override fun onClick(v: View?) {
        Logger.d("$TAG $v.id clicked")
    }


}