package ipvc.estg.myapplication.aplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ipvc.estg.myapplication.ComponenteApplication
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.CreateActivity
import ipvc.estg.myapplication.models.CreateProductLot
import ipvc.estg.myapplication.viewModel.ComponenteViewModel
import ipvc.estg.myapplication.viewModel.ComponenteViewModelFactory
import kotlinx.android.synthetic.main.activity_info_component_pending.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InfoComponentPending : AppCompatActivity() {

    private val componenteViewModel: ComponenteViewModel by viewModels {
        ComponenteViewModelFactory((application as ComponenteApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_component_pending)
        val referenceNumber = intent.getStringExtra(PARAM_Reference_Number).toString()

        GlobalScope.launch {
            getProduct(referenceNumber)
        }

        buttonSendComponent.setOnClickListener {
            sendComponent(referenceNumber)
        }

        buttonEditComponent.setOnClickListener {
            editMode()
        }

        buttonDeleteComponent.setOnClickListener {
            deleteByRefNum()
        }

        btnHelpPengingComponent.setOnClickListener {
            createDialogHelp()
        }


    }

    private fun deleteByRefNum() {
        val referenceNumber = findViewById<EditText>(R.id.valueReferenceNumberPending).text.toString()
        GlobalScope.launch {
            componenteViewModel.deleteById(referenceNumber)
            val intent = Intent(this@InfoComponentPending, ListPending::class.java)
            startActivity(intent)
        }

    }

    private fun editMode() {
        val valueDesignationInfoComponent = findViewById<EditText>(R.id.valueDesignationPending)
        val valueTypeInfoComponent = findViewById<EditText>(R.id.valueProductTypePending)
        val valueQuantidadeInfoComponent = findViewById<EditText>(R.id.valueInitialAmountPending)
        val valueIdPending = findViewById<EditText>(R.id.valueIdPending)
        val valueQuantidadePending = findViewById<EditText>(R.id.valueQuantidadePending)
        val valueDesignationActivityPending =
            findViewById<EditText>(R.id.valueDesignationActivityPending)

        checkboxYesPending.isEnabled = true
        checkboxNoPending.isEnabled = true

        checkboxYesPending.setOnClickListener {
            checkboxYesPending.isChecked = true
            checkboxNoPending.isChecked = false
        }

        checkboxNoPending.setOnClickListener {
            checkboxYesPending.isChecked = false
            checkboxNoPending.isChecked = true
        }

        valueDesignationInfoComponent.isEnabled = true
        valueTypeInfoComponent.isEnabled = true
        valueQuantidadeInfoComponent.isEnabled = true
        valueIdPending.isEnabled = true
        valueQuantidadePending.isEnabled = true
        valueDesignationActivityPending.isEnabled = true

        buttonOKComponent.visibility = View.VISIBLE

        buttonOKComponent.setOnClickListener {
            updateComponente()
        }

    }

    private fun updateComponente() {
        val referenceNumber = findViewById<EditText>(R.id.valueReferenceNumberPending)
        val designation = findViewById<EditText>(R.id.valueDesignationPending)
        val productType = findViewById<EditText>(R.id.valueProductTypePending)
        val initialAmount = findViewById<EditText>(R.id.valueInitialAmountPending)
        val id = findViewById<EditText>(R.id.valueIdPending)
        val quantidade = findViewById<EditText>(R.id.valueQuantidadePending)
        val designationActivity = findViewById<EditText>(R.id.valueDesignationActivityPending)

        if (checkboxYesPending.isChecked) {
            val isSerialNumber = true
            GlobalScope.launch {
                componenteViewModel.updateById(
                    referenceNumber.text.toString(),
                    id.text.toString(),
                    quantidade.text.toString(),
                    designationActivity.text.toString(),
                    isSerialNumber,
                    designation.text.toString(),
                    productType.text.toString(),
                    initialAmount.text.toString().toInt()
                )
                runOnUiThread {
                    createPopUpSuccessUpdate(getString(R.string.success))
                }
            }
        } else {
            val isSerialNumber = false
            GlobalScope.launch {
                componenteViewModel.updateById(
                    referenceNumber.text.toString(),
                    id.text.toString(),
                    quantidade.text.toString(),
                    designationActivity.text.toString(),
                    isSerialNumber,
                    designation.text.toString(),
                    productType.text.toString(),
                    initialAmount.text.toString().toInt()
                )
                runOnUiThread {
                    createPopUpSuccessUpdate(getString(R.string.success))
                }
            }
        }

    }


    fun getProduct(referenceNumber: String) {

        val valueReferenceNumberInfoComponent =
            findViewById<EditText>(R.id.valueReferenceNumberPending)
        val valueDesignationInfoComponent = findViewById<EditText>(R.id.valueDesignationPending)
        val valueTypeInfoComponent = findViewById<EditText>(R.id.valueProductTypePending)
        val valueQuantidadeInfoComponent = findViewById<EditText>(R.id.valueInitialAmountPending)
        val valueNameTitleInfoComponent = findViewById<TextView>(R.id.valueNameTitleInfoComponentP)
        val valueIdPending = findViewById<EditText>(R.id.valueIdPending)
        val valueQuantidadePending = findViewById<EditText>(R.id.valueQuantidadePending)
        val valueDesignationActivityPending =
            findViewById<EditText>(R.id.valueDesignationActivityPending)



        runOnUiThread {
            componenteViewModel.getComponenteById(referenceNumber)
                .observe(this, Observer {
                    if (it != null) {

                        valueReferenceNumberInfoComponent.setText(it.referenceNumber)
                        valueDesignationInfoComponent.setText(it.designation)
                        valueTypeInfoComponent.setText(it.productType)
                        valueQuantidadeInfoComponent.setText(it.initialAmount.toString())
                        valueNameTitleInfoComponent.text = it.designation
                        valueIdPending.setText(it.id)
                        valueQuantidadePending.setText(it.quantidade)
                        valueDesignationActivityPending.setText(it.designationActivity)
                        if (it.isSerialNumber) {
                            checkboxYesPending.isChecked = true
                        } else {
                            checkboxNoPending.isChecked = true
                        }
                    } else {
                        Log.d("VALE", "NULL")
                    }
                })
        }


    }

    fun sendComponent(referenceNumber: String) {
        componenteViewModel.getComponenteById(referenceNumber)
            .observe(this, Observer {
                if (it != null) {
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "")
                    val inputProductLots = HashMap<String, String>()
                    val doc: ArrayList<String> = ArrayList()

                    inputProductLots[it.id] = it.quantidade

                    val outputProductLot = CreateProductLot(
                        it.referenceNumber,
                        it.isSerialNumber,
                        it.designation,
                        it.productType,
                        it.initialAmount,
                        ArrayList()
                    )
                    val createActivity =
                        CreateActivity(
                            inputProductLots,
                            it.designationActivity,
                            outputProductLot.referenceNumber,
                            outputProductLot.isSerialNumber,
                            outputProductLot.designation,
                            outputProductLot.productType,
                            outputProductLot.initialAmount,
                            outputProductLot.documents
                        )

                    val request = ServiceBuilder.buildService(APIService::class.java)

                    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                        addFormDataPart("designation", createActivity.designation)
                        createActivity.inputProductLots.forEach { inputProductLot ->
                            addFormDataPart("inputProductLots[${inputProductLot.key}]", inputProductLot.value)
                        }
                        addFormDataPart("outputReferenceNumber", createActivity.outputReferenceNumber)
                        addFormDataPart("outputIsSerialNumber", createActivity.outputIsSerialNumber.toString())
                        addFormDataPart("outputProductType", createActivity.outputProductType)
                        addFormDataPart("outputInitialAmount", createActivity.outputInitialAmount.toString())
                        addFormDataPart("outputDesignation", createActivity.outputDesignation)

                        createActivity.outputDocuments.forEachIndexed { index, file ->
                            //Handle documents, follow AddComponent logic
//                            addFormDataPart("documents", "$index." + file.extension, file.readBytes().toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, file.readBytes().size))
                        }
                    }.build()

                    val call = request.createActivity("Bearer $token", requestBody)

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
                            createPopUpSuccess(getString(R.string.success), referenceNumber)
                        }
                    })

                } else {
                    Log.d("VALE", "NULL")
                }
            })
    }

    fun createPopUp(msg: String) {
        val dialog = Dialog(this@InfoComponentPending)
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
            val intent = Intent(this@InfoComponentPending, MainActivity::class.java)
            startActivity(intent)
        }

    }

    fun createPopUpErro(msg: String) {
        val dialog = Dialog(this@InfoComponentPending)
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

    fun createPopUpSuccess(msg: String, referenceNumber: String) {
        val dialog = Dialog(this@InfoComponentPending)
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
            GlobalScope.launch {
                componenteViewModel.deleteById(referenceNumber)
            }
            val intent = Intent(this@InfoComponentPending, ListPending::class.java)
            startActivity(intent)
        }

    }

    fun createPopUpSuccessUpdate(msg: String) {
        val dialog = Dialog(this@InfoComponentPending)
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
            val intent = Intent(this@InfoComponentPending, ListPending::class.java)
            startActivity(intent)
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
                val intent = Intent(this@InfoComponentPending, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@InfoComponentPending, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@InfoComponentPending, ListComponents::class.java)
                startActivity(intent)
                true
            }
//            R.id.nav_pending -> {
//                val intent = Intent(this@InfoComponentPending, ListPending::class.java)
//                startActivity(intent)
//                true
//            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun createDialogHelp() {
        val dialog = Dialog(this@InfoComponentPending)
        dialog.setContentView(R.layout.dialog_pending_component)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<TextView>(R.id.iconClosePopUpPendingComponent).setOnClickListener {
            dialog.dismiss()
        }

    }

}