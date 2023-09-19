package com.bisht.projectloom.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bisht.projectloom.R
import com.bisht.projectloom.databinding.ActivityMyProfileBinding
import com.bisht.projectloom.firebase.FirestoreClass
import com.bisht.projectloom.firebase.StorageClass
import com.bisht.projectloom.models.User
import com.bisht.projectloom.utils.Constants
import com.bumptech.glide.Glide
import java.io.IOException


class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        FirestoreClass().loadUserData(this)
        binding.ivProfileUserImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )

            }
        }
        binding.btnUpdate.setOnClickListener {
            if(mSelectedImageFileUri != null ){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
        }else{
            Toast.makeText(this,"Oops!, you just denied the permission for storage. "+
                    "You can allow it from settings",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()
        var anyChangesMade = false
        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image ){
            userHashMap[Constants.IMAGE] = mProfileImageUrl
            anyChangesMade = true
        }

        if( binding.etName.toString() != mUserDetails.name ){
            userHashMap[Constants.NAME] = binding.etName.text.toString()
            anyChangesMade = true
        }
        if( binding.etMobile.toString() != mUserDetails.mobile.toString() ){
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
            anyChangesMade = true
        }
        if(anyChangesMade)
            FirestoreClass().updateUserProfileData(this,userHashMap)


    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        mSelectedImageFileUri?.let { uri ->
            Constants.getFileExtension(this,uri)?.let { fileExtension ->
                StorageClass().uploadFile(this,uri, fileExtension)
                    .addOnSuccessListener { taskSnapshot ->
                        Log.e(
                            "Firebase Image URL",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                        )
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                Log.e("Downloadable Image URL", uri.toString())
                                mProfileImageUrl = uri.toString()
                                updateUserProfileData()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this@MyProfileActivity,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                        hideProgressDialog()
                    }
            }
        }
    }
    private fun showImageChooser(){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null ) {
            mSelectedImageFileUri = result.data!!.data
            setProfileImage(mSelectedImageFileUri.toString())
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
            it.title = resources.getString(R.string.my_profile_title)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setProfileImage(uri:String){
        try{
            Glide
                .with(this)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }


    fun setUserDataInUi(user: User) {
        mUserDetails = user
        mProfileImageUrl = user.image
        setProfileImage(user.image)
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if(user.mobile != 0L ){
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}