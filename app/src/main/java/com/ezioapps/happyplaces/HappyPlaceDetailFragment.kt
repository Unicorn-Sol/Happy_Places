package com.ezioapps.happyplaces

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_happy_place_detail.*


class HappyPlaceDetailFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_happy_place_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        arguments?.let {
            if(HappyPlaceDetailFragmentArgs.fromBundle(it).imageArg !=null){
                iv_place_image.setImageURI(Uri.parse(HappyPlaceDetailFragmentArgs.fromBundle(it).imageArg))

            }


            tv_description.text = HappyPlaceDetailFragmentArgs.fromBundle(it).descriptionArg
            tv_location.text = HappyPlaceDetailFragmentArgs.fromBundle(it).LocationArf
        }



    }

}