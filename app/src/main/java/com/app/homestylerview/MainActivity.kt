package com.app.homestylerview

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.homestylerview.databinding.ActivityMainBinding
import com.app.homestylerview.ui.arview.MyArFragment
import com.app.homestylerview.ui.favorite.FavoriteFragment
import com.app.homestylerview.ui.gallery.GalleryFragment
import com.app.homestylerview.ui.home.HomeFragment
import com.app.homestylerview.ui.login.LoginFragment
import com.app.homestylerview.ui.profile.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(3000)
        installSplashScreen()



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

// Verificar si el permiso está concedido
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            // Solicitar el permiso
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // Aquí invocamos el comportamiento predeterminado
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
                R.id.navigation_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                R.id.navigation_log -> Toast.makeText(this, "Log", Toast.LENGTH_SHORT).show()
            }
            true
        }
        fragmentManager=supportFragmentManager
        openFragment(HomeFragment())

        binding.fab.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchARFragment() // Si el permiso ya está concedido, inicia el fragmento de AR
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
            /*Toast.makeText(this, "AR View", Toast.LENGTH_SHORT).show()
            val fragment:Fragment = MyArFragment()

            val fragmentTransaction: FragmentTransaction= fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()*/

            /*supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Usa el ID de tu contenedor de fragmentos
                .addToBackStack(null) // Esto permite regresar al fragmento anterior con el botón "back"
                .commit()*/
        }
        // Verificar si el usuario está autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // Usuario no autenticado, redirigir a pantalla de inicio de sesión
            openLoginFragment()
            return
        }

        // Si el usuario está autenticado, continuar con la configuración habitual
        /*val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_gallery, R.id.navigation_favorite, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigation_home -> openFragment(HomeFragment())
            R.id.navigation_gallery -> openFragment(GalleryFragment())
            R.id.navigation_favorite -> openFragment(FavoriteFragment())
            R.id.navigation_profile -> openFragment(ProfileFragment())
            R.id.navigation_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.navigation_log -> Toast.makeText(this, "Log", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }

    /*override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                super.getOnBackPressedDispatcher().onBackPressed()
        }
    }*/

    private fun openFragment(fragment:Fragment){
        val fragmentTransaction: FragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    // Método para manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permiso concedido, inicia el fragmento de AR
                launchARFragment()
            } else {
                Toast.makeText(this, "Camera permission is required to use AR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para iniciar el fragmento de AR
    private fun launchARFragment() {
        Toast.makeText(this, "AR View", Toast.LENGTH_SHORT).show()
        val fragment:Fragment = MyArFragment()

        val fragmentTransaction: FragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun openLoginFragment() {
        val fragment:Fragment = LoginFragment()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}