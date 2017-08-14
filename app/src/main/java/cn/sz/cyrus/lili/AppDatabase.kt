package cn.sz.cyrus.lili

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by chenlei2 on 2017/6/26 0026.
 */
@Database(entities = arrayOf(ChatMsg::class),version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getChatMsgDao():IChatMsgDao

    companion object{
        private var INSTANCE: AppDatabase? = null
        @JvmStatic fun getInMemoryDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,"lili.db").allowMainThreadQueries().build()
            }
            return INSTANCE!!
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}