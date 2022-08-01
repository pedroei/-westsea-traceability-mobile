package ipvc.estg.myapplication.aplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.CreateActivity
import ipvc.estg.myapplication.models.CreateProductLot
import kotlinx.android.synthetic.main.activity_add_component.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val PARAM_referenceNumber = "referenceNumber"
const val PARAM_isSerialNumber = "isSerialNumber"
const val PARAM_designation = "designation"
const val PARAM_type = "type"
const val PARAM_amount = "amount"
const val PARAM_Reference_Number_Parent = "parent"


class AddComponent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_component)

        checkboxNoAddComponent.isChecked = true
        checkboxYesAddComponent.setOnClickListener {
            checkboxYesAddComponent.isChecked = true
            checkboxNoAddComponent.isChecked = false
        }

        checkboxNoAddComponent.setOnClickListener {
            checkboxYesAddComponent.isChecked = false
            checkboxNoAddComponent.isChecked = true
        }

        btnHelpAddComponent.setOnClickListener {
            createDialogHelp()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.bottom_navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_scan -> {
                val intent = Intent(this@AddComponent, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@AddComponent, ListComponents::class.java)
                startActivity(intent)
                true
            }
//            R.id.nav_pending -> {
//                val intent = Intent(this@AddComponent, ListPending::class.java)
//                startActivity(intent)
//                true
//            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun sendToAssociateComponent(view: View) {

        val referenceNumber = findViewById<EditText>(R.id.referenceNumberAddComponent)
        val designation = findViewById<EditText>(R.id.designationAddComponent)
        val type = findViewById<EditText>(R.id.productTypeAddComponent)
        val amount = findViewById<EditText>(R.id.initialAmountAddComponent)

        if (referenceNumber.text.isEmpty() || designation.text.isEmpty() || type.text.isEmpty() || amount.text.isEmpty()) {
            createPopUpErro(getString(R.string.missing_fields))
        } else {

            var isSerialNumber = true
            if (checkboxYesAddComponent.isChecked) {
                isSerialNumber = true
            } else if (checkboxNoAddComponent.isChecked) {
                isSerialNumber = false
            }
            val referenceNumberParent = intent.getStringExtra(PARAM_Reference_Number_PARENT)
            if (!referenceNumberParent.isNullOrEmpty()) {
                val intent = Intent(this@AddComponent, AssociateComponent::class.java).apply {
                    putExtra(PARAM_referenceNumber, referenceNumber.text.toString())
                    putExtra(PARAM_isSerialNumber, isSerialNumber.toString())
                    putExtra(PARAM_designation, designation.text.toString())
                    putExtra(PARAM_type, type.text.toString())
                    putExtra(PARAM_amount, amount.text.toString())
                    putExtra(PARAM_Reference_Number_Parent, referenceNumberParent)

                }
                startActivity(intent)
            } else {
                val intent = Intent(this@AddComponent, AssociateComponent::class.java).apply {
                    putExtra(PARAM_referenceNumber, referenceNumber.text.toString())
                    putExtra(PARAM_isSerialNumber, isSerialNumber.toString())
                    putExtra(PARAM_designation, designation.text.toString())
                    putExtra(PARAM_type, type.text.toString())
                    putExtra(PARAM_amount, amount.text.toString())
                }
                startActivity(intent)
            }


        }
    }

    fun createPopUp(msg: String) {
        val dialog = Dialog(this@AddComponent)
        dialog.setContentView(R.layout.pop_up)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.popup_window_text.text = msg
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.popup_ok_btt).setOnClickListener {
            dialog.dismiss()
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .clear()
                .apply()
            val intent = Intent(this@AddComponent, MainActivity::class.java)
            startActivity(intent)
        }

    }

    fun createPopUpErro(msg: String) {
        val dialog = Dialog(this@AddComponent)
        dialog.setContentView(R.layout.pop_up)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.popup_window_text.text = msg
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.popup_ok_btt).setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun createDialogHelp() {
        val dialog = Dialog(this@AddComponent)
        dialog.setContentView(R.layout.dialog_add_component)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<TextView>(R.id.iconClosePopUpAddComponent).setOnClickListener {
            dialog.dismiss()
        }

    }

    fun createPopUpSuccess(msg: String) {
        val dialog = Dialog(this@AddComponent)
        dialog.setContentView(R.layout.pop_up)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.popup_window_text.text = msg
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.popup_ok_btt).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@AddComponent, ListComponents::class.java)
            startActivity(intent)
        }

    }

    fun createProduct(view: View) {
        val referenceNumber = findViewById<EditText>(R.id.referenceNumberAddComponent).text
        val designation = findViewById<EditText>(R.id.designationAddComponent).text
        val type = findViewById<EditText>(R.id.productTypeAddComponent).text
        val amount = findViewById<EditText>(R.id.initialAmountAddComponent).text

        if (referenceNumber.isEmpty() || designation.isEmpty() || type.isEmpty() || amount.isEmpty()) {
            createPopUpErro(getString(R.string.missing_fields))
            return
        }
        GlobalScope.launch {

            val sharedPreferences: SharedPreferences =
                getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            val createProductLot = CreateProductLot(
                referenceNumber.toString(),
                checkboxYesAddComponent.isChecked,
                designation.toString(),
                type.toString(),
                amount.toString().toInt(),
                ArrayList()
            )

            val request = ServiceBuilder.buildService(APIService::class.java)
            val call = request.createProduct("Bearer $token", createProductLot)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (!response.isSuccessful) {
                        if (response.code() == 400) {
                            createPopUpErro(getString(R.string.error_invalid_request))
                        } else if (response.code() == 403) {
                            createPopUp(getString(R.string.invalid_token))
                        } else {
                            createPopUpErro(getString(R.string.error))
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    createPopUpSuccess(getString(R.string.success))
                }
            })


        }
    }

}