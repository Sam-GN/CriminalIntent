package com.ganji.criminalintent

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private const val TAG = "CrimeListFragment"


class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())
   //private var adapter: CrimeAdapter? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list,container,false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
      //  updateUI()
        return view
    }

override fun onViewCreated(view: View, savedInstanceState: Bundle?){
   super.onViewCreated(view, savedInstanceState)
   crimeListViewModel.crimeListLiveData.observe(
       viewLifecycleOwner,
      Observer { crimes ->
          crimes?.let {
              Log.i(TAG,"Got crimes ${crimes.size}")
              updateUI(crimes)
          }
      }

   )
}

private fun updateUI(crimes: List<Crime>) {
//private fun updateUI(){
    //val crimes = crimeListViewModel.crimes
   adapter = CrimeAdapter(crimes)
   crimeRecyclerView.adapter = adapter
}

private inner class CrimeHolder(view: View, viewType: Int)
   : RecyclerView.ViewHolder(view),View.OnClickListener {

   private lateinit var crime: Crime



   val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
   val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
   private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)



   init {

       if(viewType==0){
           itemView.setOnClickListener(this)

       }
       else {

           var policeButton: Button = itemView.findViewById(R.id.crime_police_button)
           policeButton.setOnClickListener {
               Toast.makeText(context, "Police Called", Toast.LENGTH_SHORT).show()
           }
           itemView.setOnClickListener(this)
       }


   }

   @RequiresApi(Build.VERSION_CODES.O)
   fun bind(crime: Crime) {
       this.crime = crime
       titleTextView.text = this.crime.title
   //    dateTextView.text =this.crime.date.toString() //DateFormat.getDateInstance().format("yyyy-MM-dd hh:mm:ss a").toString()

       solvedImageView.visibility = if (crime.isSolved) {

           View.VISIBLE
       } else {
           View.GONE
       }

   /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           val localDateTime = LocalDateTime.parse("2018-12-14T09:55:00")
           val formatter = DateTimeFormatter.ofPattern("mm.dd.yyyy")
           dateTextView.text = formatter.format(localDateTime)
       }*/

/*

       val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
       val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
       dateTextView.text = formatter.format(parser.parse("2018-12-14T09:55:00"))
*/

       val sdf = SimpleDateFormat("EEEE,MMM,dd,YYYY HH:mm:ss")
       val d = Date()
       dateTextView.text  = sdf.format(d)

   }

override fun onClick(v: View?) {
  Toast.makeText(context,"${crime.title} pressed!",Toast.LENGTH_SHORT).show()
}

}

private inner class CrimeAdapter (var crimes: List<Crime>)
:RecyclerView.Adapter<CrimeHolder>(){

override fun getItemCount() = crimes.size


@RequiresApi(Build.VERSION_CODES.O)
override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
 val crime = crimes[position]
 holder.bind(crime)
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

  if (viewType == 0) {
      val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
      return CrimeHolder(view,viewType)
}
  else{

          val view = layoutInflater.inflate(R.layout.list_item_crime_police, parent, false)
          return CrimeHolder(view,viewType)
      }


}

override fun getItemViewType(position: Int): Int {

  when {
      crimes[position].requiresPolice -> return 1
      else -> return 0
  }

}

}

companion object {
fun newInstance(): CrimeListFragment {
  return CrimeListFragment()
}
}
}