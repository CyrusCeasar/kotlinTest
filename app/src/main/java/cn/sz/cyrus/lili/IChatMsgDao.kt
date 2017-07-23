package cn.sz.cyrus.wemz

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

/**
 * Created by chenlei2 on 2017/6/26 0026.
 */
@Dao
interface IChatMsgDao {
    @Query("SELECT * FROM ChatMsg")
    fun getAll():List<ChatMsg>

    @Insert
    fun insertChatMsg(chatMsg:ChatMsg):Long
    @Update
    fun updateChatMsg(chatMsg:ChatMsg):Int
}