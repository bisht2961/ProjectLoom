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

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
): Dialog(context) {

    private var adapter: LabelColorListItemsAdapter? = null

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

        titleTextView.setText(title)
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        recyclerViewList.adapter = adapter
        adapter!!.onClickListener(object : LabelColorListItemsAdapter.OnClickListener{
            override fun onClick(position: Int, color: String) {
                onItemSelected(color)
                dismiss()
            }
        })
    }
    protected abstract fun onItemSelected(color:String)
}