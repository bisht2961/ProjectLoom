package com.bisht.projectloom.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R
import com.bisht.projectloom.models.SelectedMember
import com.bisht.projectloom.models.User
import com.bumptech.glide.Glide

open class CardMembersListAdapter (
    private val contex: Context,
    private val list: ArrayList<SelectedMember>,
    private val assignMembers: Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater
                .from(contex)
                .inflate(R.layout.item_card_selected_member,parent,false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if( holder is MyViewHolder ){
            val addMemberImageView: ImageView = holder.itemView.findViewById(R.id.iv_add_member)
            val selectedMemberImageView: ImageView = holder.itemView.findViewById(R.id.iv_selected_member_image)

            if( position == list.size-1 && assignMembers){
                addMemberImageView.visibility = View.VISIBLE
                selectedMemberImageView.visibility = View.GONE
            }else{
                addMemberImageView.visibility = View.GONE
                selectedMemberImageView.visibility = View.VISIBLE

                Glide
                    .with(contex)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(selectedMemberImageView)

            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null ){
                    onClickListener!!.onClick()
                }
            }
        }
    }
    fun onClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}