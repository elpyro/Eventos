package com.example.eventos


import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eventos.databinding.ActivityMainBinding
import com.example.eventos.modelos.Modelo_usuario
import com.example.eventos.ui.inicio.Inicio
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


@Suppress("DEPRECATION")
class PantallaPrincipal : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: NavigationView
    companion object {
        lateinit var nombreUsuario: String
        lateinit var perfilUsaurio: String
        lateinit var idUsuario: String
        lateinit var apline_usuario : String
        lateinit var usuario_validus_referido: String
        lateinit var usuario_telefono: String

    }

    //https://www.youtube.com/watch?v=IwdjCApjIzA&t=442s autenticacion
    var firebaseAutenticacion: FirebaseAuth? = null
    var authStateListener: FirebaseAuth.AuthStateListener? = null
    var dataQuery: Query? = null

    val REQUEST_CODE = 1234
    var provinder = Arrays.asList(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
         navView  = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setBackgroundResource(R.drawable.fondo);
        navView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.amarillo)));
        navView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));




        login();
//        Thread.sleep(1000)



    }

    private fun login() {
        firebaseAutenticacion = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = firebaseAutenticacion!!.currentUser
            if (user != null) {
                val Correo = user.email

                //verificar perfil segun el correo
                val myRefe = FirebaseDatabase.getInstance().reference
                dataQuery = myRefe.child("Usuarios").orderByChild("correoGmail").equalTo(Correo)
                    .limitToFirst(1)
                dataQuery!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (userSnapshot in dataSnapshot.children) {
                                val usuario: Modelo_usuario? = userSnapshot.getValue(
                                    Modelo_usuario::class.java
                                )
                                nombreUsuario = usuario?.nombre.toString()
                                perfilUsaurio = usuario?.perfil.toString()
                                idUsuario = usuario?.id.toString()
                                binding.appBarMain.toolbar.setTitle("Hola $nombreUsuario")
                                usuario_validus_referido=usuario?.validusUsuario.toString()
                                apline_usuario=usuario?.apline.toString();
                                usuario_telefono= usuario?.telefono.toString()

                                Inicio.linearlayout_todosLosUsuarios.visibility = View.VISIBLE
                                navView.getMenu()
                                    .setGroupVisible(R.id.menu_inicio, true)
                                if(perfilUsaurio.equals("Administrador")) {
                                    navView.getMenu()
                                        .setGroupVisible(R.id.menu_administrador, true)
                                }else{
                                    navView.getMenu()
                                        .setGroupVisible(R.id.menu_administrador, false)
                                }
                            }
                            return
                        } else {
                            perfilUsaurio = "Usuario no registrado"
                            navView.getMenu()
                                .setGroupVisible(R.id.menu_administrador, false)
                            navView.getMenu()
                                .setGroupVisible(R.id.menu_inicio, false)
                            Inicio.linearlayout_todosLosUsuarios.visibility = View.GONE
                            Toast.makeText(applicationContext, "Usuario no registrado", Toast.LENGTH_SHORT)
                                .show()
                            binding.appBarMain.toolbar.setTitle("Usuario no registrado")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            } else {

//                    textView_credenciales.setVisibility(View.VISIBLE);
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(provinder)
                        .setIsSmartLockEnabled(false).build(), REQUEST_CODE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAutenticacion!!.addAuthStateListener(authStateListener!!)
    }

    override fun onPause() {
        super.onPause()
        firebaseAutenticacion!!.removeAuthStateListener(authStateListener!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
