package com.bisht.projectloom.adapters

import android.app.AlertDialog
import android.content.ClipData.Item
import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bisht.projectloom.R
import com.bisht.projectloom.activities.TaskListActivity
import com.bisht.projectloom.models.Task
import java.util.Collections

open class TaskListItemAdapters(
    private val context: Context,
    private var list: ArrayList<Task>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task,parent,false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if( holder is MyViewHolder ){
            val addTaskList = holder.itemView.findViewById<TextView>(R.id.tv_add_task_list)
            val taskItemLinearLayout = holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item)
            val titleViewLinearLayout = holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view)
            val taskListTitle = holder.itemView.findViewById<TextView>(R.id.tv_task_list_title)
            val addTaskListNameCardView = holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name)
            val closeListNameButton = holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name)
            val doneListNameButton = holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name)
            val editListNameButton = holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name)
            val closeEditableViewImageButton = holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view)
            val taskListNameEditText = holder.itemView.findViewById<EditText>(R.id.et_task_list_name)
            val editTaskListNameEditText = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name)
            val editTaskViewListName = holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name)
            val doneEditListNameButton = holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name)
            val deleteListButton = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list)
            val addCardTextView = holder.itemView.findViewById<TextView>(R.id.tv_add_card)
            val addCardView = holder.itemView.findViewById<CardView>(R.id.cv_add_card)
            val closeCardViewImageButton = holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name)
            val doneAddCardImageButton = holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name)
            val cardNameEditText = holder.itemView.findViewById<EditText>(R.id.et_card_name)
            val cardListRecyclerView = holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list)

            if( position == list.size - 1 ){
                addTaskList.visibility = View.VISIBLE
                taskItemLinearLayout.visibility = View.GONE

            }else{
                addTaskList.visibility = View.GONE
                taskItemLinearLayout.visibility = View.VISIBLE
            }

            taskListTitle.text = model.title
            addTaskList.setOnClickListener {
                addTaskList.visibility = View.GONE
                addTaskListNameCardView.visibility = View.VISIBLE
            }

            closeListNameButton.setOnClickListener {
                addTaskList.visibility = View.VISIBLE
                addTaskListNameCardView.visibility = View.GONE
            }
            doneListNameButton.setOnClickListener {
                val listName = taskListNameEditText.text.toString()
                if( listName.isNotEmpty() ){
                    if( context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter List Name.",
                        Toast.LENGTH_SHORT).show()
                }

            }
            editListNameButton.setOnClickListener {
                editTaskListNameEditText.setText(model.title)
                titleViewLinearLayout.visibility = View.GONE
                editTaskViewListName.visibility = View.VISIBLE
            }
            closeEditableViewImageButton.setOnClickListener {
                titleViewLinearLayout.visibility = View.VISIBLE
                editTaskViewListName.visibility = View.GONE
            }
            doneEditListNameButton.setOnClickListener {
                val listName = editTaskListNameEditText.text.toString()
                if( listName.isNotEmpty() ){
                    if( context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }else{
                    Toast.makeText(context,"Please Enter List Name.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            deleteListButton.setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }
            addCardTextView.setOnClickListener {
                addCardTextView.visibility = View.GONE
                addCardView.visibility = View.VISIBLE
            }
            closeCardViewImageButton.setOnClickListener {
                addCardTextView.visibility = View.VISIBLE
                addCardView.visibility = View.GONE
            }
            doneAddCardImageButton.setOnClickListener {
                val cardName = cardNameEditText.text.toString()
                if( cardName.isNotEmpty() ){
                    if( context is TaskListActivity){
                        context.addCardToTaskList(position,cardName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter Card Name.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            cardListRecyclerView.layoutManager =
                LinearLayoutManager(context)
            cardListRecyclerView.setHasFixedSize(true)
            val adapter = CardListItemsAdapters(context,model.cards)
            cardListRecyclerView.adapter = adapter
            adapter.setOnClickListener(
                object : CardListItemsAdapters.OnClickListener{
                    override fun onClick(cardPosition: Int) {
                        if(context is TaskListActivity ){
                            context.cardDetails(position, cardPosition )
                        }
                    }
                }
            )
            val dividerItemDecoration = DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL)
            cardListRecyclerView.addItemDecoration(dividerItemDecoration)

            val helper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,0
                ){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition
                        if(mPositionDraggedFrom == -1){
                            mPositionDraggedFrom = draggedPosition
                        }
                        mPositionDraggedTo = targetPosition
                        Collections.swap(list[position].cards,draggedPosition,targetPosition)
                        adapter.notifyItemMoved(draggedPosition,targetPosition)
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        if(mPositionDraggedFrom != -1 && mPositionDraggedTo != -1
                            && mPositionDraggedTo != mPositionDraggedFrom){
                            (context as TaskListActivity).updateCardsInTaskList(
                                position,
                                list[position].cards
                            )
                        }
                        mPositionDraggedFrom = -1
                        mPositionDraggedTo = -1
                    }
                }
            )
            helper.attachToRecyclerView(cardListRecyclerView)

        }
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

    private fun alertDialogForDeleteList(position: Int,title:String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){ dialogInterface, which ->
            dialogInterface.dismiss()
            if ( context is TaskListActivity ){
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this*Resources.getSystem().displayMetrics.density).toInt()
}