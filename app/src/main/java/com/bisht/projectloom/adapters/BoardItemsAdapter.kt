package com.bisht.projectloom.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R
import com.bisht.projectloom.models.Board
import com.bumptech.glide.Glide

open class BoardItemsAdapter (private val context: Context, private var list:ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_board,
                parent,
                false))

    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if( holder is MyViewHolder ){

            val boardImage = holder.itemView.findViewById<ImageView>(R.id.iv_item_board_image)
            val name = holder.itemView.findViewById<TextView>(R.id.tv_item_board_name)
            val createdBy = holder.itemView.findViewById<TextView>(R.id.tv_item_board_created_by)
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(boardImage)
            name.text = model.name
            createdBy.text = buildString {
                                append("Create by: ")
                                append(model.createBy)
                            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null ){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }
    interface OnClickListener{
        fun onClick(position: Int, model:Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}