package com.ganji.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters
import com.ganji.criminalintent.Crime
import com.ganji.criminalintent.CrimeDao

@Database(entities = [Crime::class], version = 1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase(){

    abstract fun crimeDao(): CrimeDao

}