package com.bisht.projectloom.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R
import com.bisht.projectloom.models.User
import com.bisht.projectloom.utils.Constants
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

open class MembersListAdapter(
    private val context: Context,
    private val list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = list[position]
        if( holder is MyViewHolder ){
            val memberImage = holder.itemView
                .findViewById<CircleImageView>(R.id.iv_member_image)
            val memberName = holder.itemView
                .findViewById<TextView>(R.id.tv_member_name)
            val memberEmail = holder.itemView
                .findViewById<TextView>(R.id.tv_member_email)
            val selectedMember = holder.itemView.findViewById<ImageView>(R.id.iv_selected_member)
            memberName.setText(model.name)
            memberEmail.setText(model.email)
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(memberImage)
            if(model.selected){
                selectedMember.visibility = View.VISIBLE
            }else{
                selectedMember.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null ){
                    if( model.selected ){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int,user: User,action: String)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}