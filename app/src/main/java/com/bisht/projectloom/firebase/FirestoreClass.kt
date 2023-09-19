package com.bisht.projectloom.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.bisht.projectloom.activities.CardDetailsActivity
import com.bisht.projectloom.activities.CreateBoardActivity
import com.bisht.projectloom.activities.MainActivity
import com.bisht.projectloom.activities.MembersActivity
import com.bisht.projectloom.activities.MyProfileActivity
import com.bisht.projectloom.activities.SignInActivity
import com.bisht.projectloom.activities.SignUpActivity
import com.bisht.projectloom.activities.TaskListActivity
import com.bisht.projectloom.models.Board
import com.bisht.projectloom.models.Card
import com.bisht.projectloom.models.User
import com.bisht.projectloom.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val userId = Auth().getCurrentUser()

    fun registerUser(activity: SignUpActivity,userInfo:User){
        mFirestore.collection(Constants.USERS)
            .document(userId)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFirestore.collection(Constants.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                loggedInUser?.let {
                    when(activity){
                        is SignInActivity ->{
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity ->{
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                        is MyProfileActivity ->{
                            activity.setUserDataInUi(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener {e ->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser","Error signing in user")
            }
    }
    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){
        mFirestore.collection(Constants.USERS)
            .document(userId)
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Profile Data Updated Successfully!")
                Toast.makeText(activity,"Profile Updated successfully!",Toast.LENGTH_SHORT).show()
                when(activity){
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e->
                when(activity){
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)
                Toast.makeText(activity,"Error Updating Info",Toast.LENGTH_SHORT).show()
            }
    }

    fun createBoard(activity: CreateBoardActivity,board: Board){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board created successfully.")
                Toast.makeText(activity,"Board Created Successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error Creating Board.",exception)
            }
    }

    fun getBoardsList(activity:MainActivity){
        val currentUserId = Auth().getCurrentUser()
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,currentUserId)
            .get()
            .addOnSuccessListener {
                doc ->
                Log.i(activity.javaClass.simpleName,doc.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                doc.documents.forEach {snap ->
                    val board = snap.toObject(Board::class.java)!!
                    board.documentId = snap.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while loading board",e)
            }
    }

    fun getBoardDetails(activity:TaskListActivity,documentId:String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    doc ->
                Log.i(activity.javaClass.simpleName,doc.toString())
                val board = doc.toObject(Board::class.java)!!
                board.documentId = doc.id
                activity.boardDetails(board)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while loading board",e)
            }
    }

    fun addUpdateTaskList(activity: Activity,board:Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"TaskList Updated successfully.")
                if(activity is TaskListActivity){
                    activity.addUpdateTaskListSuccess()
                }else if (activity is CardDetailsActivity ){
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener { e ->
                if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                }else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName,"Error Updating TaskList.",e)
            }
    }
    fun getAssignedMembersListDetails(activity: Activity,assignedTo: ArrayList<String>){
        mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {doc ->
                Log.e(activity.javaClass.simpleName,doc.documents.toString())
                val usersList: ArrayList<User> = ArrayList()
                doc.documents.forEach {
                    val user = it.toObject(User::class.java)!!
                    usersList.add(user)
                }
                if(activity is MembersActivity )
                    activity.setUpMembersList(usersList)
                else if ( activity is TaskListActivity )
                    activity.boardMembersDetailsList(usersList)
            }
            .addOnFailureListener {e->
                if(activity is MembersActivity )
                    activity.hideProgressDialog()
                else if( activity is TaskListActivity )
                    activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName,"Error while loading members",e)
            }
    }
    fun getMemberDetails(activity: MembersActivity,email:String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {doc ->
                if(doc.size()>0){
                    val user = doc.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting user details",e)
            }
    }
    fun assignMemberToBoard(
        activity: MembersActivity,board:Board,user: User){

        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while adding user to board ",e)
            }

    }
}