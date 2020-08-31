package com.ezioapps.happyplaces.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface HappyDao {

    @Insert
    suspend fun addHappyPlace(happyPlaceData: HappyPlaceData)

    @Update
    suspend fun updateHappyPlace(happyPlaceData: HappyPlaceData)

    @Query("select * from HappyPlaceData")
    suspend fun getHappyPlaces() : List<HappyPlaceData>

}
