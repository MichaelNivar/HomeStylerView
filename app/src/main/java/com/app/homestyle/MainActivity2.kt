package com.app.homestyle

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.app.homestyle.databinding.ActivityMain2Binding
import com.app.homestyle.databinding.ActivityMainBinding
import com.app.homestyle.ui.category.CategoryFragment
import com.app.homestyle.ui.home.HomeFragment
import com.app.homestyle.ui.login.LoginFragment
import com.app.homestyle.ui.product.ProductFragment
import com.app.homestyle.ui.product.ProductListFragment
import com.app.homestyle.ui.profile.ProfileFragment
import com.app.homestyle.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMain2Binding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)

        checkUserSession()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout2.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout2.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        fragmentManager=supportFragmentManager
        setSupportActionBar(binding.toolbar)

        val toggle= ActionBarDrawerToggle(this, binding.drawerLayout2, binding.toolbar, R.string.nav_open, R.string.nav_close)

        binding.drawerLayout2.addDrawerListener(toggle)
        toggle.syncState()

        binding.bottomNavigation.background=null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_category -> openFragment(CategoryFragment())
                R.id.navigation_product -> openFragment(ProductFragment())
                R.id.nav_profile -> openFragment(ProfileFragment())
                R.id.nav_productlist -> openFragment(ProductListFragment())
            }
            true
        }
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun checkUserSession() {
        lifecycleScope.launch {
            val loggedInUser = sessionManager.loggedInUser.first()
            if (loggedInUser != null) {
                // Usuario logueado, navegar al HomeFragment
                openFragment(CategoryFragment())
            } else {
                // Usuario no logueado, navegar al LoginFragment
                openFragment(LoginFragment())
            }
        }
    }
}