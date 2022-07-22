package ipvc.estg.myapplication.aplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R

class ScanPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_page)
        checkLogin()
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    private fun checkLogin() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("tokenSP",Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("token","")

        if (token!!.isEmpty()){
            val intent = Intent(this@ScanPage, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun logout(view: View) {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .clear()
            .apply()
        val intent = Intent(this@ScanPage, MainActivity::class.java)
        startActivity(intent)

    }

    private fun shouldAllowBack():Boolean{
        val sharedPreferences: SharedPreferences = getSharedPreferences("tokenSP",Context.MODE_PRIVATE)

        val token =sharedPreferences.getString("token","")
        return !token!!.isNotEmpty()
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.bottom_navigation_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_scan -> {
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@ScanPage, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@ScanPage, ListComponents::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_pending -> {
                val intent = Intent(this@ScanPage, ListPending::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun scanQRCode(view: View) {
        val intent = Intent(this@ScanPage, ScanQRCode::class.java)
        startActivity(intent)
    }


}