package com.example.notetakingapp.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.databinding.NotesItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import render.animations.Bounce
import render.animations.Render
import render.animations.Zoom

class rvAdapter(private val list: List<rvDataMode>, private var context: Context, private var listner: onItemClickListner) :RecyclerView.Adapter<rvAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //inflate the NotesItems
       val binding = NotesItemsBinding.inflate(LayoutInflater.from(context),parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size //return size of total items in list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.titleItem.text = list[position].title
        holder.binding.descriptionItem.text = list[position].description
        loadImage(holder) //call Image method
    }


    //interface to click on Some view Items
    interface onItemClickListner{
        fun onItemClick(data: rvDataMode)
        fun onItemDelete(data: rvDataMode)
    }

    //ViewHolder to hold views
    inner  class MyViewHolder(var binding: NotesItemsBinding):RecyclerView.ViewHolder(binding.root){

        //initialization of viewItems
        init {
            binding.cardView.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    listner.onItemClick(rvDataMode(list[position].title,list[position].description,list[position].itemKey))
                }
            }
            binding.delete.setOnClickListener {
                // Create Render Class
                val render = Render(context)
                render.setAnimation(Zoom().In(binding.delete))
                render.start()
                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemDelete(rvDataMode("","",list[position].itemKey))
                }
            }
        }
    }

    //Load Image method
    private fun loadImage(holder: MyViewHolder){
        val databaseReference = FirebaseDatabase.getInstance().reference
        val auth = FirebaseAuth.getInstance()
        var storageRef =  Firebase.storage.reference
        val currentUser = auth.currentUser

        //load Image as per User Choice
        currentUser.let { user->
            val key = databaseReference.child("Users").child(user!!.uid).child("ImageCheck").child("isDefault")
            key.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isDefault  = snapshot.getValue(Boolean::class.java)
                    storageRef = if(isDefault == true){
                        storageRef.child("sticky.png")
                    } else{
                        storageRef.child("Photo")
                    }
                    storageRef.downloadUrl.addOnSuccessListener{
                        Picasso.get().load(it.toString()).into(holder.binding.image)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }


    }
}