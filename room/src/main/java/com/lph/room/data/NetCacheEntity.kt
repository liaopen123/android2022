package com.lph.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//这个是定义table的实体
@Entity(tableName = "netCache")
data class NetCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "url", typeAffinity = ColumnInfo.TEXT) val url: String?,
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.TEXT) val data: String?,
)