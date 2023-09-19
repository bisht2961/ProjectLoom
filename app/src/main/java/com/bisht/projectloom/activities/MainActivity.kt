package com.bisht.projectloom.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bisht.projectloom.R
import com.bisht.projectloom.adapters.BoardItemsAdapter
import com.bisht.projectloom.databinding.ActivityMainBinding
import com.bisht.projectloom.databinding.AppBarMainBinding
import com.bisht.projectloom.firebase.Auth
import com.bisht.projectloom.firebase.FirestoreClass
import com.bisht.projectloom.models.Board
import com.bisht.projectloom.models.User
import com.bisht.projectloom.utils.Constants
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserName:String
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        backPressed()
        setUpActionBar()
        binding.navView.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(
            Constants.PROJECTLOOM_PREFERENCE, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences
            .getBoolean(Constants.FCM_TOKEN_UPDATED,false)
        if(tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this,true)
        }else{
            FirebaseMessaging.getInstance().token.addOnSuccessListener{ result ->
                if(result != null){
                    updateFCMToken(result)
                }
            }
        }

        FirestoreClass().loadUserData(this,true)

        val fabCreateBoard = findViewById<FloatingActionButton>(R.id.fab_create_board)
        fabCreateBoard.setOnClickListener {
            val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            resultLauncher.launch(intent)
        }
    }

    fun populateBoardsListToUI(boardsList:ArrayList<Board>){
        hideProgressDialog()
        val mainContent = binding.appBarMain.mainContent
        mainContent.tvNoBoardsAvailable.visibility = View.GONE
        val rvBoardsList = mainContent.rvBoardsList

        if( boardsList.size > 0 ){
            rvBoardsList.visibility = View.VISIBLE
            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this,boardsList)
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
            rvBoardsList.adapter = adapter
        }else{
            rvBoardsList.visibility = View.GONE
            mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }
    fun updateNavigationUserDetails(loggedInUser: User,readBoardList:Boolean) {
        hideProgressDialog()
        mUserName = loggedInUser.name
        val navUserImage = findViewById<CircleImageView>(R.id.nav_user_image)
        val navUsername = findViewById<TextView>(R.id.nav_user_name)
        Glide
            .with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)
        navUsername.text = loggedInUser.name

        if(readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this,true)
    }

    private fun updateFCMToken(token: String){
        val usersHashMap = HashMap<String,Any>()
        usersHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,usersHashMap)
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if( binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    doubleBackToExit()
                }
            }
        })
    }

    private fun setUpActionBar(){
        val toolbarMain = binding.appBarMain.toolbarMainActivity
        setSupportActionBar(toolbarMain)
        toolbarMain.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbarMain.setNavigationOnClickListener{
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        if( binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                resultLauncher.launch(Intent(this,MyProfileActivity::class.java))
            }
            R.id.nav_sign_out->{
                Auth().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().loadUserData(this)
        }else if( result.resultCode == Constants.CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }
}