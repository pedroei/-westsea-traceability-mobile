package ipvc.estg.myapplication.aplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ipvc.estg.myapplication.ComponenteApplication
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.ActivityDesignation
import ipvc.estg.myapplication.models.Componente
import ipvc.estg.myapplication.models.Product
import ipvc.estg.myapplication.viewModel.ComponenteViewModel
import ipvc.estg.myapplication.viewModel.ComponenteViewModelFactory
import kotlinx.android.synthetic.main.activity_associate_component.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AssociateComponent : AppCompatActivity() {

    private val componenteViewModel: ComponenteViewModel by viewModels {
        ComponenteViewModelFactory((application as ComponenteApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_associate_component)

        backAssociateComponent.setOnClickListener {
            onBackPressed()
        }
        getRefsNums()
        getActivitiesDesignation()

    }

    private fun getRefsNums() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getAllProducts("Bearer $token")

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val refsDesignations: List<Product> = response.body()!!
                    var refsArray: Array<String> = emptyArray()
                    refsArray = refsArray.plus(getString(R.string.et_referenceNumber_addcomponent))
                    for (ref in refsDesignations) {
                        if(ref.availableQuantity>0) {
                            refsArray = refsArray.plus(ref.referenceNumber)
                        }
                    }
                    spinnerDownRefs(refsArray)
                } else {
                    createPopUp(getString(R.string.invalid_token))
                }

            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@AssociateComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getActivitiesDesignation() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getAllActivitiesDesignation("Bearer $token")

        call.enqueue(object : Callback<List<ActivityDesignation>> {
            override fun onResponse(
                call: Call<List<ActivityDesignation>>,
                response: Response<List<ActivityDesignation>>
            ) {
                if (response.isSuccessful) {
                    val activityDesignations: List<ActivityDesignation> = response.body()!!
                    var activitiesArray: Array<String> = emptyArray()
                    activitiesArray = activitiesArray.plus(getString(R.string.et_activity))
                    for (activity in activityDesignations) {
                        activitiesArray = activitiesArray.plus(activity.designation)
                    }
                    spinnerDown(activitiesArray)
                } else {
                    createPopUp(getString(R.string.invalid_token))
                }

            }

            override fun onFailure(call: Call<List<ActivityDesignation>>, t: Throwable) {
                Toast.makeText(this@AssociateComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun spinnerDown(activitiesArray: Array<String>) {
        val spinner = findViewById<Spinner>(R.id.activityAssociateComponent)
        if (spinner != null) {

            val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, activitiesArray
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup?
                ): View? {
                    val view = super.getDropDownView(position, convertView, parent)
                    val tv = view as TextView
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY)
                    } else {
                        tv.setTextColor(Color.BLACK)
                    }
                    return view
                }
            }

            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.adapter = spinnerArrayAdapter;


            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    if (position == 0) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }

            }
        }
    }

    fun spinnerDownRefs(activitiesArray: Array<String>) {
        val spinner = findViewById<Spinner>(R.id.parentAssociateComponent)
        if (spinner != null) {

            val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, activitiesArray
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup?
                ): View? {
                    val view = super.getDropDownView(position, convertView, parent)
                    val tv = view as TextView
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY)
                    } else {
                        tv.setTextColor(Color.BLACK)
                    }
                    return view
                }
            }

            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.adapter = spinnerArrayAdapter;


            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    if (position == 0) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }

            }

            val referenceNumberParent = intent.getStringExtra(PARAM_Reference_Number_Parent)

            val n: Int = spinner.adapter.count
            val options = HashMap<Int,String>()
            for (i in 0 until n) {
                val option: String = spinner.adapter.getItem(i) as String
                options[i] = option
            }

            if(!referenceNumberParent.isNullOrEmpty()){
                for (option in options){
                    if (option.value==referenceNumberParent){
                        spinner.setSelection(option.key)
                    }
                }
            }
        }
    }


    fun createActivity(view: View) {
        GlobalScope.launch {
            val referenceNumberParent =
                findViewById<Spinner>(R.id.parentAssociateComponent).selectedItem.toString()
            val idInputProductLots = getProduct(referenceNumberParent).ID
            while (idInputProductLots.isEmpty()) {
                delay(1)
            }
            if (idInputProductLots != "") {
                val designationActivity =
                    findViewById<Spinner>(R.id.activityAssociateComponent).selectedItem.toString()
                val quantity =
                    findViewById<EditText>(R.id.quantityAssociateComponent).text.toString()
                val referenceNumber = intent.getStringExtra(PARAM_referenceNumber).toString()
                val isSerialNumber = intent.getStringExtra(PARAM_isSerialNumber).toBoolean()
                val designation = intent.getStringExtra(PARAM_designation).toString()
                val type = intent.getStringExtra(PARAM_type).toString()
                val amount = intent.getStringExtra(PARAM_amount).toString().toInt()


                if (idInputProductLots.isEmpty() || quantity.isEmpty() || designationActivity == getString(
                        R.string.et_activity
                    )
                ) {
                    runOnUiThread {
                        createPopUpErro(getString(R.string.missing_fields))
                    }
                } else {
                    componenteViewModel.insert(
                        Componente(
                            referenceNumber,
                            idInputProductLots,
                            quantity,
                            designationActivity,
                            isSerialNumber,
                            designation,
                            type,
                            amount
                        )
                    )
                    runOnUiThread {
                        createPopUpSuccess(getString(R.string.success))
                    }

                }
            }
        }
    }

    suspend fun getProduct(referenceNumber: String): Product {

        var canGO = false

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getProductByRN("Bearer $token", referenceNumber)
        val doc: ArrayList<String> = ArrayList()

        var product = Product(
            "",
            "",
            false,
            "",
            "",
            0,
            0,
            doc,
            ""
        )

        call.enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                if (response.isSuccessful) {
                    product = response.body()!!
                    canGO = true
                } else {
                    createPopUpErro(getString(R.string.errorDontExistRN))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                canGO = true
                Toast.makeText(this@AssociateComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        while (!canGO) {
            delay(1)
        }
        return product
    }


    fun createPopUp(msg: String) {
        val dialog = Dialog(this@AssociateComponent)
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
            val intent = Intent(this@AssociateComponent, MainActivity::class.java)
            startActivity(intent)
        }

    }

    fun createPopUpErro(msg: String) {
        val dialog = Dialog(this@AssociateComponent)
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

    fun createPopUpSuccess(msg: String) {
        val dialog = Dialog(this@AssociateComponent)
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
            val intent = Intent(this@AssociateComponent, ListPending::class.java)
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
                val intent = Intent(this@AssociateComponent, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@AssociateComponent, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@AssociateComponent, ListComponents::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_pending -> {
                val intent = Intent(this@AssociateComponent, ListPending::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}