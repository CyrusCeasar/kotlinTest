package cn.sz.cyrus.wemz

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by chenlei2 on 2017/6/26 0026.
 */
@Entity
class ChatMsg {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
    var createTime:Long = 0
    var msg:String = ""
    var translMsg:String =""
    var sendToRobot:Boolean = true
}