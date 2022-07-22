package ipvc.estg.myapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.aplication.ScanPage
import ipvc.estg.myapplication.models.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkLogin()
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    private fun checkLogin() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("token", "")

        if (token!!.isNotEmpty()) {
            val intent = Intent(this@MainActivity, ScanPage::class.java)
            startActivity(intent)
        }
    }

    private fun shouldAllowBack(): Boolean {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("token", "")
        return !token!!.isEmpty()
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed()
        }
    }

    fun login(view: View) {
        val username = findViewById<EditText>(R.id.userName).text
        val password = findViewById<EditText>(R.id.password).text
        val params = HashMap<String, String>()
        params["username"] = username.toString()
        params["password"] = password.toString()


        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.login(params)

        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful) {
                    val token: Token = response.body()!!
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putString("token", token.access_token)
                        .apply()
                    val intent = Intent(this@MainActivity, ScanPage::class.java)
                    startActivity(intent)

                } else {
                    createPopUp()
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun showPassword(view: View) {
        val passwordInput = findViewById<EditText>(R.id.password)
        val inputTypeValue: Int = passwordInput.inputType

        if (inputTypeValue == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            passwordInput.inputType = InputType.TYPE_CLASS_TEXT
        } else {
            passwordInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    fun createPopUp() {
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.pop_up)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.popup_ok_btt).setOnClickListener {
            dialog.dismiss()
        }

    }
}