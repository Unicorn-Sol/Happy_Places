package com.ezioapps.happyplaces

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.ezioapps.happyplaces.db.HappyDatabase
import com.ezioapps.happyplaces.db.HappyPlaceData
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_add_happy_places.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.AllPermission
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.measureTimedValue


class AddHappyPlacesFragment : BaseFragment() , View.OnClickListener{


    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var saveImageToUri : Uri? = null

    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value.
    private var mLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if(!Places.isInitialized())
            context?.let { Places.initialize(it, resources.getString(R.string.google_maps_api_key)) }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->

            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateInView()
        }

        et_date.setOnClickListener(this)
        textView.setOnClickListener(this)
        imageView.setOnClickListener(this)
        materialButton.setOnClickListener(this)
        et_location.setOnClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_add_happy_places, container, false)
    }

    override fun onClick(v: View?) {
      when(v?.id) {
          R.id.et_date -> {
              DatePickerDialog(
                  requireContext(),
                  dateSetListener,
                  cal.get(Calendar.YEAR),
                  cal.get(Calendar.MONTH),
                  cal.get(Calendar.DAY_OF_MONTH)
              ).show()
          }

          R.id.textView -> {
              val pictureDialog =
                  AlertDialog.Builder(context)
              pictureDialog.setTitle("Select Action")
              pictureDialog.setItems(arrayOf("Select photo from gallery", "Capture photo from camera")
              ) { _, which ->
                  when(which){
                      0 -> choosePhotoFromGallery()
                      1 ->takePhotoFromCamera()
                  }
              }.show()
          }

          R.id.imageView -> {
              AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog).setTitle("Select Action")
                  .setItems(arrayOf("Select photo from gallery", "Capture photo from camera")) { _, which ->
                  when(which){
                      0 -> choosePhotoFromGallery()
                      1 ->takePhotoFromCamera()
                  }
              }.show()

          }

          R.id.materialButton ->{
              buttonClicked()
          }

          R.id.et_location -> {

              try {

                  val fields = listOf(
                      Place.Field.ID,
                      Place.Field.NAME,
                      Place.Field.LAT_LNG,
                      Place.Field.ADDRESS

                  )

                  val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,
                  fields).build(requireContext())
                  startActivityForResult(intent, PLACE_AUTO_COMPLETE_REQUEST_CODE)


              }catch (e:Exception){
                  e.printStackTrace()
              }
          }


      }
    }

    private fun buttonClicked() {
        val title = et_title.text.toString()
        val description = et_description.text.toString()
        val date = et_date.text.toString()
        val location = et_location.text.toString()






        if (title.isEmpty()){
            et_title.error = "Title is needed"
            et_title.requestFocus()
        }

        if (description.isEmpty()){
            et_description.error = "Description cannot be empty"
            et_description.requestFocus()
        }

        if (date.isNullOrEmpty()) {
            et_date.error = "Choose Date"
            et_date.requestFocus()
        }

        if (location.isNullOrEmpty()){
            et_location.error = "Mention Location"

        }

        try{
            val imageUri = saveImageToUri.toString()
            val happyPlace = HappyPlaceData(0, title,imageUri ,description, date, location, null, null)

            launch {
                context?.let{
                    HappyDatabase.funData(it)?.getHappyDao()?.addHappyPlace(happyPlace)
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
        }



        materialButton.let {
            it.findNavController().navigate(R.id.action_addHappyPlacesFragment_to_homeFragment)
        }

    }

    private fun choosePhotoFromGallery() {

        Dexter.withContext(requireContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener( object : MultiplePermissionsListener{
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    if(p0!!.areAllPermissionsGranted()){
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent,GALLERY )
                        Toast.makeText(requireContext(), "permissions granted. Please Wait",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                    AlertDialog.Builder(requireContext(),android.R.style.Theme_Material_Dialog).setMessage("Looks like you have disabled permission. Grant permission in settings to continue")
                        .setPositiveButton("GO TO SETTINGS"){ _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:"+ context?.packageName)

                            startActivity(intent)
                    }.setCancelable(true)
                        .show()
                }
            }).onSameThread().check()
    }

    private fun updateInView() {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateFinal = sdf.format(cal.time).toString()
    et_date.setText(dateFinal)


    }

    private fun takePhotoFromCamera(){

        Dexter.withContext(requireContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .withListener( object : MultiplePermissionsListener{
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    if(p0!!.areAllPermissionsGranted()){
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent,CAMERA )
                        Toast.makeText(requireContext(), "permissions granted. Please wait!",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                    AlertDialog.Builder(requireContext(),android.R.style.Theme_Material_Dialog).setMessage("Looks like you have disabled permission. Grant permission in settings to continue")
                        .setPositiveButton("GO TO SETTINGS"){ _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:"+ context?.packageName)

                            startActivity(intent)
                        }.setCancelable(true)
                        .show()
                }
            }).onSameThread().check()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY){

            val uri = data?.data
            val image = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)

            saveImageToUri = saveImageToStorage(image)
            Log.e("pathUri", "path :: $saveImageToUri")
            imageView.setImageBitmap(image)
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA){
            val thumbNail = data?.extras!!.get("data") as Bitmap
            saveImageToUri = saveImageToStorage(thumbNail)
            Log.e("pathUri", "path :: $saveImageToUri")
            imageView.setImageBitmap(thumbNail)
        }

        else if (resultCode == Activity.RESULT_OK && requestCode == PLACE_AUTO_COMPLETE_REQUEST_CODE){

            val place = Autocomplete.getPlaceFromIntent(data!!)
            et_location.setText(place.address)
            mLatitude = place.latLng!!.latitude
            mLongitude = place.latLng!!.longitude

        }
    }
    companion object{
        const val GALLERY = 1
        const val  CAMERA = 2
        const val IMAGE_DIRECTORY = "ImagesDirectory"
        const val PLACE_AUTO_COMPLETE_REQUEST_CODE = 3
    }

    private fun saveImageToStorage(bitmap: Bitmap):Uri{

        val wrapper = ContextWrapper(activity?.applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
         file = File(file, "${System.currentTimeMillis()}.jpg")

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        }catch (e : IOException){
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)

    }
}


