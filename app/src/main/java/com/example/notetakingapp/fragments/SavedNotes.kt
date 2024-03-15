package com.example.notetakingapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.EditNotes
import com.example.notetakingapp.databinding.FragmentSavedNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class SavedNotes : Fragment(), rvAdapter.onItemClickListner{
    private lateinit var binding: FragmentSavedNotesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val currentUser = auth.currentUser

        //Retrieve data from database to mutable list
        currentUser.let { user->
            val key = databaseReference.child("Users").child(user!!.uid).child("Notes")
            key.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notesList= mutableListOf<rvDataMode>()
                    for(notesnapshot in snapshot.children){
                        val data = notesnapshot.getValue(rvDataMode::class.java)
                        data.let {
                            if (it != null) {
                                notesList.add(it)
                            }
                        }
                    }
                    notesList.reverse()
                    recyclerView.adapter = rvAdapter(notesList,requireContext(),this@SavedNotes)

                }
                override fun onCancelled(error: DatabaseError) {}
            })
            }

        binding = FragmentSavedNotesBinding.inflate(inflater,container,false)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }
    companion object {}

    //show data from firebase on EditNotes Activity
    override fun onItemClick(data: rvDataMode) {
        val intent = Intent(requireContext(), EditNotes::class.java)
        intent.putExtra("Title", data.title)
        intent.putExtra("Description", data.description)
        intent.putExtra("Key", data.itemKey)
        startActivity(intent)
    }

    //delete data from firebase
    override fun onItemDelete(data: rvDataMode) {
        val currentUser = auth.currentUser
        val dialog1 = AlertDialog.Builder(requireContext())
        dialog1.setMessage("Do you want to Delete!")
            .setPositiveButton("Delete"){dialog,id->
                currentUser.let { user->
                    databaseReference.child("Users").child(user!!.uid).child("Notes").child(data.itemKey).removeValue()
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(requireContext(), "sorry", Toast.LENGTH_SHORT).show()
                            }
                        }


                }
            }
            .setNeutralButton("Cancel"){dialog,id->

            }

        dialog1.create()
        dialog1.show()

    }
}