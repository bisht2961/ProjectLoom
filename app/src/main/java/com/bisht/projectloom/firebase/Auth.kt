package com.bisht.projectloom.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Auth {
    private var _authInstance : FirebaseAuth

    init {
        _authInstance = FirebaseAuth.getInstance()
    }
    fun signInUser(email: String,password: String):Task<AuthResult>{
        return _authInstance.signInWithEmailAndPassword(email,password)
    }
    fun createUser(email:String,password:String): Task<AuthResult> {
        return _authInstance.createUserWithEmailAndPassword(email,password)
    }
    fun signOut(){
        _authInstance.signOut()
    }
    fun getCurrentUser():String{
       var userId = ""
        _authInstance.currentUser?.let {
            userId = it.uid
        }
        return userId
    }
}