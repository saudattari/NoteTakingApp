package com.example.notetakingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.example.notetakingapp.databinding.ActivityEditNotesBinding
import com.example.notetakingapp.fragments.rvDataMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditNotes : AppCompatActivity() {
    private lateinit var binding: ActivityEditNotesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        //click on add button
        binding.add.setOnClickListener {
            val title = binding.titleEdit.text.toString()
            val description = binding.textEdit.text.toString()


            if(title.isEmpty() || description.isEmpty()){
                Toast.makeText(this, "Please Fill both fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val currentUser = auth.currentUser
                currentUser.let { user->
                    val noteKey = databaseReference.child("Users").child(user!!.uid).child("Notes").push().key
                    if(noteKey!=null){
                        databaseReference.child("Users").child(user.uid).child("Notes").child(noteKey).setValue(
                            rvDataMode(title,description,noteKey)
                        )
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                                    binding.titleEdit.setText("")
                                    binding.textEdit.setText("")

                                }
                                else{
                                    Toast.makeText(this, "Check Connection! Sending Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                }
            }

        }

        val intent = intent
        val dataTitle = intent.getStringExtra("Title")
        val dataDescription = intent.getStringExtra("Description")
        val refKey = intent.getStringExtra("Key")
        binding.titleEdit.setText(dataTitle)
        binding.textEdit.setText(dataDescription)

        // click on Update Button
        binding.button2.setOnClickListener {
            val currentUser = auth.currentUser
            currentUser.let { user->
                if(refKey != null){
                databaseReference.child("Users").child(user!!.uid).child("Notes").child(refKey.toString()).setValue(rvDataMode(binding.titleEdit.text.toString(),binding.textEdit.text.toString(),refKey.toString()))
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Data updated Successfully", Toast.LENGTH_SHORT).show()
                            binding.titleEdit.setText("")
                            binding.textEdit.setText("")
                        }
                        
                    }
                }
                else{
                    Toast.makeText(this, "Sorry Key is empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(Intent(this,MainActivity::class.java))
        }
        return super.onKeyDown(keyCode, event)
    }

}