package com.ezioapps.happyplaces.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database( entities = [HappyPlaceData::class],
    version = 1,
    exportSchema = false)
abstract class HappyDatabase : RoomDatabase(){

    abstract fun getHappyDao() : HappyDao

    companion object{

        @Volatile private var instance : HappyDatabase? = null

         fun funData(context: Context): HappyDatabase? {
            if (instance == null){
                synchronized(this){
                    instance = Room.databaseBuilder(context.applicationContext, HappyDatabase::class.java, "myDB").build()
                }
            }
            return instance
        }


    }
}