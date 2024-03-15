package com.example.notetakingapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.notetakingapp.databinding.ActivityMainBinding
import com.example.notetakingapp.fragments.SavedNotes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private var checkBackPress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.bottom)
        auth = FirebaseAuth.getInstance()

        setDrawer() //call method
        replace(SavedNotes()) //replace SavedNotesFragment onStart

        //clickListener on newButton to switch on EditNotes Activity
        binding.New.setOnClickListener {
            startActivity(Intent(this, EditNotes::class.java))
        }

    }

    //set Drawer and ActionBar
    private fun setDrawer() {
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.toolBar)
        binding.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolBar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        toggle.syncState()
    }

    //when press backKey
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            checkBackPress +=1
            when(checkBackPress){
                1->{
                    if(drawerLayout.isDrawerOpen(GravityCompat.START)){ drawerLayout.closeDrawers()}
                    else  {replace(SavedNotes())}
                    }
                2->{
                    if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawers()
                    else{
                        checkBackPress = 0
                        System.exit(0) }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //on Navigation Items click
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.logout -> {
                drawerLayout.closeDrawers()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()
                GoogleSignIn.getClient(this, gso).signOut()
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.settings->{
                drawerLayout.closeDrawers()
                val intent = Intent(this, UploadImage::class.java)
                startActivity(intent)
            }

            R.id.share->{
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val body = "Check Out My New Note Taking App https://github.com/saudattari/NoteTakingApp"
                intent.putExtra(Intent.EXTRA_TEXT ,body)

                startActivity(Intent.createChooser(intent,"Share Via"))
            }
        }
        return true
    }

    //replace fragments
    private fun replace(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val tr = fragmentManager.beginTransaction()
        tr.replace(R.id.frm, fragment)

        tr.commit()

    }

}

