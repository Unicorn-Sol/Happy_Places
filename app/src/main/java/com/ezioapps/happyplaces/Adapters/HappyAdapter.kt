package com.ezioapps.happyplaces.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ezioapps.happyplaces.HomeFragmentDirections
import com.ezioapps.happyplaces.R
import com.ezioapps.happyplaces.db.HappyPlaceData
import kotlinx.android.synthetic.main.item_happy_place.view.*

class HappyAdapter( private val items : List<HappyPlaceData>):RecyclerView.Adapter<HappyAdapter.HappyViewHolder>() {





    class HappyViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HappyViewHolder {
        return HappyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_happy_place,parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HappyViewHolder, position: Int) {

        val item = items[position]
        if (items[position].image!= null){
            holder.view.iv_place_image.setImageURI(Uri.parse(item.image))
        }
        holder.view.tvTitle.text = item.title
        holder.view.tvDescription.text = item.description

        holder.view.setOnClickListener{

            val action = HomeFragmentDirections.actionHomeFragmentToHappyPlaceDetailFragment(item.image,item.description, item.location )
            Navigation.findNavController(it).navigate(action)
        }
    }
}