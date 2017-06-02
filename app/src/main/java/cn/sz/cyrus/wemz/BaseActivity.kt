package cn.sz.cyrus.wemz

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.orhanobut.logger.Logger
import com.umeng.analytics.MobclickAgent

/**
 * Created by 41264 on 05/21/17.
 */
open class BaseActivity :AppCompatActivity(), View.OnClickListener {

    val TAG = BaseActivity::class.simpleName

    override fun onClick(v: View?) {
        Logger.d("$TAG $v.id clicked")
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(TAG)
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(TAG)
        MobclickAgent.onPause(this)
    }
;

}