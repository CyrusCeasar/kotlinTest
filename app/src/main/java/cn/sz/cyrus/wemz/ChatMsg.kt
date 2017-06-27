package cn.sz.cyrus.wemz

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by chenlei2 on 2017/6/26 0026.
 */
@Entity
data class ChatMsg(var msg: String,
              var to: Int) {
    constructor() : this("", MASTER)
    companion object TO {
        val MASTER = 0
        val ROBOT = 1
    }

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var createTime: Long = System.currentTimeMillis()
    var translMsg: String = ""
}