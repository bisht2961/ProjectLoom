package com.bisht.projectloom.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.bisht.projectloom.R
import com.bisht.projectloom.firebase.Auth
import com.bisht.projectloom.databinding.ActivitySignUpBinding
import com.bisht.projectloom.firebase.FirestoreClass
import com.bisht.projectloom.models.User


class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }

    }
    fun userRegisteredSuccess(){
        Toast.makeText(this,"Successfully Registered",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        finish()
    }

    private fun registerUser(){
        val name:String = binding.etName.text.toString().trim{ it <= ' '}
        val email:String = binding.etEmail.text.toString().trim{ it <= ' '}
        val password:String = binding.etPassword.text.toString().trim{ it <= ' '}
        if( validateForm(name,email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            Auth().createUser(email,password).addOnCompleteListener { task ->

                if(task.isSuccessful){
                    val firebaseUser = task.result.user
                    val regEmail = firebaseUser!!.email.toString()
                    val user = User(firebaseUser.uid,name,regEmail)
                    FirestoreClass().registerUser(this,user)
                }else{
                    Toast.makeText(this,"Registration Fail",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun validateForm(name:String,email:String,password:String): Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter your name")
                false
            }
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
}