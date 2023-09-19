package com.bisht.projectloom.firebase

import android.app.Activity
import android.net.Uri
import com.bisht.projectloom.activities.CreateBoardActivity
import com.bisht.projectloom.activities.MyProfileActivity
import com.bisht.projectloom.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class StorageClass {

    private val storageInstance =
        FirebaseStorage.getInstance().reference

    fun uploadFile(activity: Activity,uri: Uri,fileExtension:String): UploadTask {
        var path = ""
        when(activity){
            is MyProfileActivity -> {
                path = Constants.USERS_IMAGE
            }
            is CreateBoardActivity ->{
                path = Constants.BOARDS_IMAGE
            }
        }
        return storageInstance.child(path).child(""+System.currentTimeMillis()+"."+fileExtension)
            .putFile(uri)
    }
}