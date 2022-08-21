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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.adapters.ListAdapter
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.ListProducts
import ipvc.estg.myapplication.models.Product
import kotlinx.android.synthetic.main.activity_list_components.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val PARAM_Reference_Number = "referenceNumber"

class ListComponents : AppCompatActivity() {

    private lateinit var myListProducts: ArrayList<ListProducts>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_components)

        var activitiesArray: Array<String> = emptyArray()

        activitiesArray = activitiesArray.plus(getString(R.string.search_rn))
        activitiesArray = activitiesArray.plus(getString(R.string.search_designation))
        activitiesArray = activitiesArray.plus(getString(R.string.search_type))

        spinnerDown(activitiesArray)


        GlobalScope.launch {
            getProducts()
        }
        rnListComps.addTextChangedListener {
            GlobalScope.launch {
                getProductsSearch()
            }
        }

        btnHelpListComp.setOnClickListener {
            createDialogHelp()
        }

    }

    fun spinnerDown(activitiesArray: Array<String>) {
        val spinner = findViewById<Spinner>(R.id.filterListComp)
        if (spinner != null) {

            val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, activitiesArray
            ) {
                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup?
                ): View? {
                    val view = super.getDropDownView(position, convertView, parent)
                    val tv = view as TextView
                    tv.setTextColor(Color.BLACK)
                    return view
                }
            }

            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.adapter = spinnerArrayAdapter;

        }
    }

    suspend fun getProducts() {

        var canGO = false

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getAllProducts("Bearer $token")

        var products = listOf<Product>()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    products = response.body()!!
                    canGO = true
                } else {
                    createPopUp(getString(R.string.invalid_token))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                canGO = true
                Toast.makeText(this@ListComponents, "${t.message}", Toast.LENGTH_SHORT).show()
            }


        })
        val loading = findViewById<GifImageView>(R.id.loading)
        runOnUiThread {
            loading.visibility = View.VISIBLE
        }
        while (!canGO) {
            delay(1)
        }
        runOnUiThread {
            loading.visibility = View.INVISIBLE
            myListProducts = ArrayList()
            if (products.isNotEmpty()) {
                for (product in products) {
                    myListProducts.add(
                        ListProducts(
                            product.designation,
                            product.referenceNumber,
                            product.productType
                        )
                    )
                }
                listLine.adapter = ListAdapter(myListProducts)
                listLine.layoutManager = LinearLayoutManager(this@ListComponents)
            }
            if (myListProducts.isEmpty()) {
                Log.d("VALE", "ERROR")
            } else {
                val adapter = ListAdapter(myListProducts)
                listLine.adapter = adapter
                adapter.setOnItemClickListener(object : ListAdapter.onItemClickListener {
                    override fun onItemClick(idComp: TextView) {
                        val intent = Intent(this@ListComponents, InfoComponent::class.java).apply {
                            putExtra(PARAM_Reference_Number, idComp.text.toString())
                        }
                        startActivity(intent)
                    }
                })
                listLine.layoutManager = LinearLayoutManager(this@ListComponents)
            }
        }
    }

    suspend fun getProductsSearch() {
        val filtro = findViewById<Spinner>(R.id.filterListComp).selectedItem.toString()
        var canGO = false

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getAllProducts("Bearer $token")

        var products = listOf<Product>()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    products = response.body()!!
                    canGO = true
                } else {
                    createPopUp(getString(R.string.invalid_token))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                canGO = true
                Toast.makeText(this@ListComponents, "${t.message}", Toast.LENGTH_SHORT).show()
            }


        })

        while (!canGO) {
            delay(1)
        }

        runOnUiThread {
            myListProducts = ArrayList()

            if (products.isNotEmpty()) {
                if (filtro == getString(R.string.search_rn)) {
                    for (product in products) {
                        if (product.referenceNumber.take(rnListComps.text.length) == rnListComps.text.toString()) {
                            myListProducts.add(
                                ListProducts(
                                    product.designation,
                                    product.referenceNumber,
                                    product.productType
                                )
                            )
                        }
                    }
                } else if (filtro == getString(R.string.search_designation)) {
                    for (product in products) {
                        if (product.designation.take(rnListComps.text.length) == rnListComps.text.toString()) {
                            myListProducts.add(
                                ListProducts(
                                    product.designation,
                                    product.referenceNumber,
                                    product.productType
                                )
                            )
                        }
                    }
                } else if (filtro == getString(R.string.search_type)) {
                    for (product in products) {
                        if (product.productType.take(rnListComps.text.length) == rnListComps.text.toString()) {
                            myListProducts.add(
                                ListProducts(
                                    product.designation,
                                    product.referenceNumber,
                                    product.productType
                                )
                            )
                        }
                    }
                }
                listLine.adapter = ListAdapter(myListProducts)
                listLine.layoutManager = LinearLayoutManager(this@ListComponents)
            }



            if (myListProducts.isEmpty()) {
                Log.d("VALE", "ERROR")
            } else {
                val adapter = ListAdapter(myListProducts)
                listLine.adapter = adapter
                adapter.setOnItemClickListener(object : ListAdapter.onItemClickListener {
                    override fun onItemClick(idComp: TextView) {
                        val intent = Intent(this@ListComponents, InfoComponent::class.java).apply {
                            putExtra(PARAM_Reference_Number, idComp.text.toString())
                        }
                        startActivity(intent)
                    }
                })
                listLine.layoutManager = LinearLayoutManager(this@ListComponents)
            }
        }
    }

    fun createPopUp(msg: String) {
        val dialog = Dialog(this@ListComponents)
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
            val intent = Intent(this@ListComponents, MainActivity::class.java)
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
                val intent = Intent(this@ListComponents, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@ListComponents, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@ListComponents, ListComponents::class.java)
                startActivity(intent)
                true
            }
//            R.id.nav_pending -> {
//                val intent = Intent(this@ListComponents, ListPending::class.java)
//                startActivity(intent)
//                true
//            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun createDialogHelp() {
        val dialog = Dialog(this@ListComponents)
        dialog.setContentView(R.layout.dialog_list_components)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<TextView>(R.id.iconClosePopUpListComponents).setOnClickListener {
            dialog.dismiss()
        }

    }
}