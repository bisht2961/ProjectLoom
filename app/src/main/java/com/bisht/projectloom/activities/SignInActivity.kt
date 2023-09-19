package com.bisht.projectloom.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.bisht.projectloom.R
import com.bisht.projectloom.firebase.Auth
import com.bisht.projectloom.databinding.ActivitySignInBinding
import com.bisht.projectloom.firebase.FirestoreClass
import com.bisht.projectloom.models.User

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setUpActionBar()
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }

    }

    private fun signInRegisteredUser(){
        val email:String = binding.etEmailSignin.text.toString().trim{ it <= ' '}
        val password:String = binding.etPasswordSignin.text.toString().trim{ it <= ' '}
        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            Auth().signInUser(email, password).addOnCompleteListener { task->
                hideProgressDialog()
                if(task.isSuccessful){
                    FirestoreClass().loadUserData(this@SignInActivity)
//                    Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Try Again",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    fun signInSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    private fun validateForm(email:String,password:String): Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter your email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter valid password")
                false
            }
            else->{
                true
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}