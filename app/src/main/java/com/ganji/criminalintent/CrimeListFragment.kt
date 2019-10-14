package com.ganji.criminalintent

import android.app.LauncherActivity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.ganji.criminalintent.database.CrimeDao
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private const val TAG = "CrimeListFragment"


class CrimeListFragment : Fragment() {

       interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter()
    private lateinit var addCrimeButton: Button
    private lateinit var noCrimeTextView: TextView


    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list,container,false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        noCrimeTextView = view.findViewById(R.id.fragment_crime_list_tv) as TextView
        addCrimeButton = view.findViewById(R.id.fragment_crime_list_btn)

        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        addCrimeButton.setOnClickListener(){
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }

        return view
    }


override fun onViewCreated(view: View, savedInstanceState: Bundle?){
   super.onViewCreated(view, savedInstanceState)
   crimeListViewModel.crimeListLiveData.observe(
       viewLifecycleOwner,
      Observer { crimes ->
          crimes?.let {
              Log.i(TAG,"Got crimes ${crimes.size}")

              if(crimes.isEmpty()){
                  crimeRecyclerView.isEnabled = false
                  crimeRecyclerView.visibility = View.GONE
                  noCrimeTextView.isEnabled = true
                  addCrimeButton.isEnabled = true

              } else {
                  crimeRecyclerView.isEnabled = true
                  crimeRecyclerView.visibility = View.VISIBLE
                  noCrimeTextView.isEnabled = false
                  addCrimeButton.isEnabled = false
              }

              updateUI(crimes.toMutableList())

          }
      }

   )
}

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


private fun updateUI(crimes: MutableList<Crime>) {
   adapter!!.submitList(crimes)

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
       solvedImageView.visibility = if (crime.isSolved) {

           View.VISIBLE
       } else {
           View.GONE
       }


       val sdf = SimpleDateFormat("EEEE,MMM,dd,YYYY HH:mm:ss")
       val d = crime.date
       dateTextView.text  = sdf.format(d)

   }





override fun onClick(v: View?) {
    callbacks?.onCrimeSelected(crime.id)
}

}

private inner class CrimeAdapter ()
    : ListAdapter<Crime, CrimeHolder>(DiffCallback()){


@RequiresApi(Build.VERSION_CODES.O)
override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
    holder.bind(getItem(position))
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

    return 0

}

}
    class DiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }

companion object {
fun newInstance(): CrimeListFragment {
  return CrimeListFragment()
}
}
}