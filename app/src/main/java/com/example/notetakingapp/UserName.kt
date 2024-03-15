package com.example.notetakingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.notetakingapp.databinding.ActivityUserNameBinding

class UserName : AppCompatActivity() {
    private lateinit var binding: ActivityUserNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.my_light_primary)
        binding.button.setOnClickListener {
            passData(binding.editTextText.text.toString())
        }
    }

    private fun passData(userName:String){
        val intent = Intent(this, MainActivity::class.java)
        val name = binding.editTextText.text.toString()
        intent.putExtra("UserName", name)
        startActivity(intent)
        finish()
    }
}