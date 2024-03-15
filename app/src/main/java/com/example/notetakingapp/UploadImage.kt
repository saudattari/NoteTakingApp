package com.example.notetakingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.notetakingapp.databinding.ActivityUploadImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class UploadImage : AppCompatActivity() {
    private lateinit var binding: ActivityUploadImageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference


       binding.button4.setOnClickListener{
           currentUser().let { user->
             databaseReference.child("Users").child(user.uid).child("ImageCheck").child("isDefault").setValue(true)
                 .addOnCompleteListener {
                     if (it.isSuccessful){
                         Toast.makeText(this, "Default Image", Toast.LENGTH_SHORT).show()
                         val ref = Firebase.storage.reference.child("sticky.png")
                         ref.downloadUrl.addOnSuccessListener{uri->
                             Picasso.get().load(uri.toString()).into(binding.image)
                         }
                     }
                 }
           }
       }

        binding.uploadImage.setOnClickListener {
            currentUser().let { user->
                databaseReference.child("Users").child(user.uid).child("ImageCheck").child("isDefault").setValue(false)
            }
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }
    }

    //check current User
    private fun currentUser():FirebaseUser{
        val currentUser = auth.currentUser
        return currentUser!!
    }


    val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            if(it.data != null){
            var ref = Firebase.storage.reference
                ref =ref.child("Photo")
                ref.putFile(it.data!!.data!!).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener{

                    Picasso.get().load(it.toString()).into(binding.image);
                    }
                }
            }
        }
    }

}