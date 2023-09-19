package com.bisht.projectloom.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String,
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.item_label_color,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if( holder is MyViewHolder ){
            val labelColorViewMain: View = holder.itemView.findViewById(R.id.label_color_view_main)
            val labelSelectedColor: ImageView = holder.itemView.findViewById(R.id.iv_label_selected_color)
            labelColorViewMain.setBackgroundColor(Color.parseColor(item))
            if( item == mSelectedColor ){
                labelSelectedColor.visibility = View.VISIBLE
            }else{
                labelSelectedColor.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null ){
                    onClickListener?.onClick(position,item)
                }
            }
        }
    }
    fun onClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(position: Int, color: String)
    }
}