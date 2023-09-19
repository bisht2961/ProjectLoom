package com.bisht.projectloom.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R
import com.bisht.projectloom.activities.TaskListActivity
import com.bisht.projectloom.models.Card
import com.bisht.projectloom.models.SelectedMember

open class CardListItemsAdapters (
    private val context: Context,
    private val list: ArrayList<Card>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false),
        )
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {

            val cardNameTextView = holder.itemView
                .findViewById<TextView>(R.id.tv_card_name)

            val viewLabelColor = holder.itemView
                .findViewById<View>(R.id.view_label_color)

            val selectedMemberRecyclerView: RecyclerView =
                holder.itemView.findViewById(R.id.rv_card_selected_members_list)

            cardNameTextView.text = model.name

            if (model.labelColor.isNotEmpty()) {
                viewLabelColor.visibility = View.VISIBLE
                viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                viewLabelColor.visibility = View.GONE
            }

            if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0) {
                val selectedMembersList: ArrayList<SelectedMember> = ArrayList()

                for (i in context.mAssignedMemberDetailList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMemberDetailList[i].id == j) {
                            val selectedMember = SelectedMember(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )
                            selectedMembersList.add(selectedMember)
                        }
                    }
                }

                if (selectedMembersList.size > 0) {
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                        selectedMemberRecyclerView.visibility = View.GONE
                    } else {
                        selectedMemberRecyclerView.visibility = View.VISIBLE
                        selectedMemberRecyclerView.layoutManager = GridLayoutManager(context, 4)
                        val adapter = CardMembersListAdapter(context, selectedMembersList, false)
                        selectedMemberRecyclerView.adapter = adapter
                        adapter.onClickListener(object : CardMembersListAdapter.OnClickListener {
                            override fun onClick() {
                                if (onClickListener != null) {
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                } else {
                    selectedMemberRecyclerView.visibility = View.GONE
                }

                holder.itemView.setOnClickListener {
                    if (onClickListener != null) {
                        Log.e("CardListItemsAdapter", "Item View Clicked")
                        onClickListener!!.onClick(position)
                    }
                }

            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick(position: Int)
    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}