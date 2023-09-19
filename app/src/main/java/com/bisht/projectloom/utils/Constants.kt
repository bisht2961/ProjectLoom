package com.bisht.projectloom.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val BOARDS: String = "boards"
    const val USERS_IMAGE: String = "users_image"
    const val BOARDS_IMAGE: String = "boards_image"
    const val READ_STORAGE_PERMISSION_CODE = 1

    const val CREATE_BOARD_REQUEST_CODE = 12
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val EMAIL: String = "email"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"

    const val BOARD_DETAIL: String = "board_detail"
    const val ID: String = "id"

    const val TASK_LIST: String = "taskList"
    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"

    const val BOARD_MEMBERS_LIST: String = "board_members_list"
    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"


    const val PROJECTLOOM_PREFERENCE: String = "ProjectLoomPreference"
    const val FCM_TOKEN_UPDATED: String = "fcmTokenUpdated"
    const val FCM_TOKEN: String = "fcmToken"

    const val FCM_BASE_URL: String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_KEY: String = "key"
    const val FCM_SERVER_KEY: String = "AAAAZxrn620:APA91bGpGu8ua6BppwiYDw4tjDufzH3T-02x4Bz34n9PTpg9EWkx5FNynFBzfkkZxD9Y-1iZKTUIUZZSy9BKdlKFXor3tdtibzXcj4FKyVN-Q7BP45UZDOzPC1zO5_3Jw1wIPELA9Dfs"
    const val FCM_KEY_TITLE: String = "title"
    const val FCM_KEY_MESSAGE: String = "message"
    const val FCM_KEY_DATA: String = "data"
    const val FCM_KEY_TO: String = "to"



    fun getFileExtension(activity: Activity,uri: Uri?): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}