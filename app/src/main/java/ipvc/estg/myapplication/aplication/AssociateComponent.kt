package ipvc.estg.myapplication.aplication

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.ActivityDesignation
import ipvc.estg.myapplication.models.CreateActivity
import ipvc.estg.myapplication.models.OutputProductLot
import ipvc.estg.myapplication.models.Product
import kotlinx.android.synthetic.main.activity_associate_component.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AssociateComponent : AppCompatActivity() {

    private var inputComponents: HashMap<String, String> = HashMap<String, String>()
    private var currentSelectedProduct: Product? = null
    private val selectedComponents: MutableList<String> = mutableListOf<String>()
    private lateinit var selectedComponentsArrayAdapter: ArrayAdapter<*>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_associate_component)

        backAssociateComponent.setOnClickListener {
            onBackPressed()
        }
        getRefsNums()

        val mListView = findViewById<ListView>(R.id.selected_components)
        selectedComponentsArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, selectedComponents
        )
        mListView.adapter = selectedComponentsArrayAdapter
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
                    var refsArray: Array<Product> = emptyArray()
                    for (ref in refsDesignations) {
                        if (ref.availableQuantity > 0) {
                            refsArray = refsArray.plus(ref)
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

    private fun getActivitiesDesignation(spinner: Spinner) {
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
                    spinnerDown(activitiesArray, spinner)
                } else {
                    createPopUp(getString(R.string.invalid_token))
                }

            }

            override fun onFailure(call: Call<List<ActivityDesignation>>, t: Throwable) {
                Toast.makeText(this@AssociateComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun spinnerDown(activitiesArray: Array<String>, spinner: Spinner) {
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

    fun spinnerDownRefs(activitiesArray: Array<Product>) {
        val spinner = findViewById<Spinner>(R.id.parentAssociateComponent)
        if (spinner != null) {

            var mappedArray: Array<String> = emptyArray()
            mappedArray = mappedArray.plus(getString(R.string.et_referenceNumber_addcomponent))
            mappedArray =
                mappedArray.plus(activitiesArray.map { "${it.designation} (${it.referenceNumber})" })

            val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, mappedArray
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
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        (view as TextView).setTextColor(Color.GRAY)
                    } else {
                        val selectedProduct =
                            activitiesArray.get(position - 1) //minus one because the first is just a label
                        currentSelectedProduct = selectedProduct
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }

            }

            val referenceNumberParent = intent.getStringExtra(PARAM_Reference_Number_Parent)

            val n: Int = spinner.adapter.count
            val options = HashMap<Int, String>()
            for (i in 0 until n) {
                val option: String = spinner.adapter.getItem(i) as String
                options[i] = option
            }

            if (!referenceNumberParent.isNullOrEmpty()) {
                for (option in options) {
                    if (option.value == referenceNumberParent) {
                        spinner.setSelection(option.key)
                    }
                }
            }
        }
    }


    fun createActivity(view: View) {
        if (inputComponents.size === 0) {
            Toast.makeText(
                this@AssociateComponent,
                getString(R.string.add_some_component),
                Toast.LENGTH_LONG
            ).show()
            return;
        }

        showActivityDesignationDialog()
    }

    fun doCreateActivity(activityDesignationValue: String) {
        GlobalScope.launch {
            if (!inputComponents.isEmpty() && activityDesignationValue != "") {
                val referenceNumber = intent.getStringExtra(PARAM_referenceNumber).toString()
                val isSerialNumber = intent.getStringExtra(PARAM_isSerialNumber).toBoolean()
                val designation = intent.getStringExtra(PARAM_designation).toString()
                val type = intent.getStringExtra(PARAM_type).toString()
                val amount = intent.getStringExtra(PARAM_amount).toString().toInt()

                if (inputComponents.isEmpty() || activityDesignationValue == "") {
                    runOnUiThread {
                        createPopUpErro(getString(R.string.add_some_activity_and_component))
                    }
                } else {
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "")

                    val outputProductLot = OutputProductLot(
                        referenceNumber,
                        isSerialNumber,
                        designation,
                        type,
                        amount,
                        ArrayList()
                    )

                    val createActivity =
                        CreateActivity(inputComponents, activityDesignationValue, outputProductLot)

                    val request = ServiceBuilder.buildService(APIService::class.java)
                    val call = request.createActivity("Bearer $token", createActivity)

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
    }

    private fun showActivityDesignationDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.choose_activity_designation, null)
        val spinner = dialogLayout.findViewById<Spinner>(R.id.activityDesignationStr)
        getActivitiesDesignation(spinner)

        with(builder) {
            setTitle(getString(R.string.choose_activity))
            setPositiveButton(getString(R.string.ok)) { dialog, which ->
                doCreateActivity(spinner.selectedItem.toString())
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            setView(dialogLayout)
            show()
        }
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
            val intent = Intent(this@AssociateComponent, ListComponents::class.java)
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
//            R.id.nav_pending -> {
//                val intent = Intent(this@AssociateComponent, ListPending::class.java)
//                startActivity(intent)
//                true
//            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun addComponent(view: View) {
        val quantity = findViewById<EditText>(R.id.quantityAssociateComponent).text;
        if (currentSelectedProduct == null || quantity.isEmpty()) {
            Toast.makeText(
                this@AssociateComponent,
                getString(R.string.provide_info),
                Toast.LENGTH_LONG
            ).show()
            return;
        }
        inputComponents.put(currentSelectedProduct!!.ID, quantity.toString())

        selectedComponents.add("${currentSelectedProduct?.designation} (x${quantity.toString()})")
        selectedComponentsArrayAdapter.notifyDataSetChanged()

        findViewById<Spinner>(R.id.parentAssociateComponent).setSelection(0)
        findViewById<EditText>(R.id.quantityAssociateComponent).setText("")
        currentSelectedProduct = null
    }
}