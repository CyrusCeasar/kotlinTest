package cn.sz.cyrus.wemz

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

/**
 * Created by chenlei2 on 2017/6/26 0026.
 */
@Dao
interface IChatMsgDao {
    fun getAll():List<ChatMsg>

    @Insert
    fun insertChatMsg(chatMsg:ChatMsg):Int
}