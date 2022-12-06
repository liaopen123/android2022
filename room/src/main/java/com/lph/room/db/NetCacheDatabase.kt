package com.lph.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lph.room.dao.NetCacheDao
import com.lph.room.data.NetCacheEntity

@Database(entities = [NetCacheEntity::class], version = 1)
abstract class NetCacheDatabase :RoomDatabase(){
    abstract fun netCacheDao():NetCacheDao

    companion object {
        @Volatile
        private var INSTANCE: NetCacheDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): NetCacheDatabase {
            val tmpInstance = INSTANCE
            if (tmpInstance != null) {
                return tmpInstance
            }
            //锁
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, NetCacheDatabase::class.java, "netCacheDb").build()
                INSTANCE = instance
                return instance
            }
        }
    }
    
}


