package com.bisht.projectloom.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bisht.projectloom.R
import com.bisht.projectloom.databinding.ActivityCreateBoardBinding
import com.bisht.projectloom.firebase.Auth
import com.bisht.projectloom.firebase.FirestoreClass
import com.bisht.projectloom.firebase.StorageClass
import com.bisht.projectloom.models.Board
import com.bisht.projectloom.utils.Constants
import com.bumptech.glide.Glide
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateBoardBinding
    private var mSelectedImageFileUri : Uri? = null
    private lateinit var mUserName: String
    private var mBoardImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {
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
        binding.btnCreate.setOnClickListener {
            if(mSelectedImageFileUri != null ){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(Auth().getCurrentUser())
        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageUrl,
            mUserName,
            "",
            assignedUsersArrayList
        )
        FirestoreClass().createBoard(this,board)

    }

    private fun uploadBoardImage(){
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
                                mBoardImageUrl = uri.toString()
                                createBoard()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                        hideProgressDialog()
                    }
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
            it.title = resources.getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
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

    private fun showImageChooser(){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null ) {
            mSelectedImageFileUri = result.data!!.data
            setProfileImage(mSelectedImageFileUri.toString())
        }
    }
    private fun setProfileImage(uri:String){
        try{
            Glide
                .with(this)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(binding.ivBoardImage)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Constants.CREATE_BOARD_REQUEST_CODE)
        finish()
    }
}