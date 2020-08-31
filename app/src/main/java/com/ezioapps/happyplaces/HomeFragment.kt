package com.ezioapps.happyplaces

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezioapps.happyplaces.Adapters.HappyAdapter
import com.ezioapps.happyplaces.db.HappyDatabase
import com.ezioapps.happyplaces.db.HappyPlaceData
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        launch{
            context?.let{
                val happyPlaces = HappyDatabase.funData(it)?.getHappyDao()?.getHappyPlaces()

                if (happyPlaces != null){
                    recycler_view_only_one.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    recycler_view_only_one.visibility = View.VISIBLE
                    recycler_view_only_one.setHasFixedSize(true)
                    tvNoHappyPlaces.visibility = View.GONE
                    recycler_view_only_one.adapter = HappyAdapter(happyPlaces)
                }
                else{
                    recycler_view_only_one.visibility = View.GONE
                    tvNoHappyPlaces.visibility = View.VISIBLE
                }
            }
        }
        addFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addHappyPlacesFragment)
        }
    }

}