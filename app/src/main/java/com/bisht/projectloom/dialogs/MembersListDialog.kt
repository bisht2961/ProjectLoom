package com.bisht.projectloom.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R
import com.bisht.projectloom.adapters.LabelColorListItemsAdapter
import com.bisht.projectloom.adapters.MembersListAdapter
import com.bisht.projectloom.models.User

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = "",
): Dialog(context) {

    private var adapter: MembersListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View){
        val titleTextView: TextView = view.findViewById(R.id.tvTitle)
        val recyclerViewList: RecyclerView = view.findViewById(R.id.rvList)
        titleTextView.text = title
        if( list.size > 0 ){
            recyclerViewList.layoutManager = LinearLayoutManager(context)
            adapter = MembersListAdapter(context, list)
            recyclerViewList.adapter = adapter
            adapter!!.setOnClickListener(object : MembersListAdapter.OnClickListener{
                override fun onClick(position: Int,user: User,action:String) {
                    onItemSelected(user,action)
                    dismiss()
                }
            })
        }
    }
    protected abstract fun onItemSelected(user: User,action:String)
}