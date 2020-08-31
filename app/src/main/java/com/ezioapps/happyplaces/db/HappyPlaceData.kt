package com.ezioapps.happyplaces.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class HappyPlaceData(

    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title : String,
    val image : String?,
    val description : String,
    val date : String,
    val location : String,
    val latitude : Double?,
    val longitude : Double?
)