package com.app.homestyle

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.app.homestyle.databinding.ActivityMainBinding
import com.app.homestyle.ui.arview.ArFragment
import com.app.homestyle.ui.favorite.FavoriteFragment
import com.app.homestyle.ui.gallery.GalleryFragment
import com.app.homestyle.ui.home.HomeFragment
import com.app.homestyle.ui.login.LoginFragment
import com.app.homestyle.ui.profile.AccountFragment
import com.app.homestyle.ui.profile.ProfileFragment
import com.app.homestyle.utils.SessionManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private var isLogin: Boolean= false
    private var string: String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)

        checkUserSession()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        setSupportActionBar(binding.toolbar)

        val toggle= ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background=null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_home -> openFragment(HomeFragment())
                R.id.navigation_gallery -> openFragment(GalleryFragment())
                R.id.navigation_favorite -> openFragment(FavoriteFragment())
                R.id.navigation_profile -> openFragment(ProfileFragment())
            }
            true
        }
        fragmentManager=supportFragmentManager
        openFragment(HomeFragment())

        binding.fab.setOnClickListener {
            openFragment(ArFragment())

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigation_home -> openFragment(HomeFragment())
            R.id.navigation_gallery -> openFragment(GalleryFragment())
            R.id.navigation_favorite -> openFragment(FavoriteFragment())
            R.id.navigation_profile -> openFragment(ProfileFragment())
            R.id.navigation_settings -> openFragment(AccountFragment())
            R.id.navigation_log -> manageSession()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }


    private fun openFragment(fragment:Fragment){
        val fragmentTransaction: FragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun checkUserSession() {
        lifecycleScope.launch {
            val loggedInUser = sessionManager.loggedInUser.first()
            if (loggedInUser != null) {
                openFragment(HomeFragment())
                updateLogIcon(isLoggedIn = true) // Cambiar a ícono de "Logout"
            } else {
                openFragment(LoginFragment())
                updateLogIcon(isLoggedIn = false) // Cambiar a ícono de "Login"
            }
        }
    }


    private fun manageSession() {
        lifecycleScope.launch {
            val loggedInUser = sessionManager.loggedInUser.first()
            if (loggedInUser != null) {
                sessionManager.clearUserSession()
                updateLogIcon(isLoggedIn = false) // Cambiar a ícono de "Login"
                string= getString(R.string.logout)
                Toast.makeText(this@MainActivity, string, Toast.LENGTH_SHORT).show()
            } else {
                openFragment(LoginFragment())
                updateLogIcon(isLoggedIn = true) // Cambiar a ícono de "Logout"
                string= getString(R.string.login)
                Toast.makeText(this@MainActivity, string, Toast.LENGTH_SHORT).show()
            }

            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is ProfileFragment) {
                currentFragment.checkUserSession()
            }
        }
    }

    private fun updateLogIcon(isLoggedIn: Boolean) {
        val navigationView = binding.navigationDrawer
        val menu = navigationView.menu
        val logMenuItem = menu.findItem(R.id.navigation_log)
        if (isLoggedIn) {
            logMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_login)
            string= getString(R.string.logout_btn)
            logMenuItem.title = string
        } else {
            logMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_logout)
            string= getString(R.string.login_btn)
            logMenuItem.title = string
        }
    }

}