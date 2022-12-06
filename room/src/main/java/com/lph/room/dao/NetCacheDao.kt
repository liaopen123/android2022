package com.lph.room.dao

import androidx.room.*
import com.lph.room.data.NetCacheEntity

@Dao
interface NetCacheDao {
    @Insert
    fun addNetCache(vararg netCacheEntity: NetCacheEntity)

    @Delete
    fun removeNetCache(vararg netCacheEntity: NetCacheEntity)

    @Update
    fun updateNetCache(vararg netCacheEntity: NetCacheEntity)

    @Query("select * from netCache")
    fun queryAllNetCache():List<NetCacheEntity>
}