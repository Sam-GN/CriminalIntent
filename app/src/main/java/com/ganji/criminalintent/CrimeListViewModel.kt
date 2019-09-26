package com.ganji.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

   /*var crimes = mutableListOf<Crime>()

   init {
      for (i in 0 until 100){
          val crime = Crime()
          crime.title = "Crime #$i"
          crime.isSolved = i % 2 == 0
          crime.requiresPolice = i%2 ==0
          //a += b is bellow
          crimes.plusAssign(crime)
      }
  }*/

  private val crimeRepository = CrimeRepository.get()
  val crimeListLiveData = crimeRepository.getCrimes()
  //val crimes = crimeRepository.getCrimes()
}