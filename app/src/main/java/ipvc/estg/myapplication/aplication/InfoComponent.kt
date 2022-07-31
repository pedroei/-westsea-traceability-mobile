package ipvc.estg.myapplication.aplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.adapters.ListAssoCompAdapter
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.CreateActivity
import ipvc.estg.myapplication.models.ListAssoComp
import ipvc.estg.myapplication.models.Product
import kotlinx.android.synthetic.main.activity_info_component.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val PARAM_Reference_Number_PARENT = "RefParent"

class InfoComponent : AppCompatActivity() {

    private lateinit var myList: ArrayList<ListAssoComp>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_component)

        GlobalScope.launch {
            val referenceNumber = intent.getStringExtra(PARAM_Reference_Number)
            getProduct(referenceNumber.toString())
        }

    }


    suspend fun getProduct(referenceNumber: String) {

        val loading = findViewById<GifImageView>(R.id.loadingInfo)
        runOnUiThread {
            loading.visibility = View.VISIBLE
        }

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
                    createPopUp(getString(R.string.invalid_token))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                canGO = true
                Toast.makeText(this@InfoComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        while (!canGO) {
            delay(1)
        }
        val valueReferenceNumberInfoComponent =
            findViewById<TextView>(R.id.valueReferenceNumberInfoComponent)
        val valueDesignationInfoComponent =
            findViewById<TextView>(R.id.valueDesignationInfoComponent)
        val valueTypeInfoComponent = findViewById<TextView>(R.id.valueTypeInfoComponent)
        val valueQuantidadeInfoComponent = findViewById<TextView>(R.id.valueQuantidadeInfoComponent)
        val valueNameTitleInfoComponent = findViewById<TextView>(R.id.valueNameTitleInfoComponent)

        runOnUiThread {
            valueReferenceNumberInfoComponent.text = product.referenceNumber
            valueDesignationInfoComponent.text = product.designation
            valueTypeInfoComponent.text = product.productType
            valueQuantidadeInfoComponent.text = product.availableQuantity.toString()
            valueNameTitleInfoComponent.text = product.designation
        }


        getProducts()
    }

    suspend fun getProducts() {

        val referenceNumber = intent.getStringExtra(PARAM_Reference_Number).toString()
        val componentDad = getComponent(referenceNumber)

        var canGO = false

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val request = ServiceBuilder.buildService(APIService::class.java)
        val call = request.getAllActivities("Bearer $token")

        var products = listOf<CreateActivity>()

        call.enqueue(object : Callback<List<CreateActivity>> {
            override fun onResponse(
                call: Call<List<CreateActivity>>,
                response: Response<List<CreateActivity>>
            ) {
                if (response.isSuccessful) {
                    products = response.body()!!
                    canGO = true
                } else {
                    createPopUp(getString(R.string.invalid_token))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<List<CreateActivity>>, t: Throwable) {
                canGO = true
                Toast.makeText(this@InfoComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }


        })

        while (!canGO) {
            delay(1)
        }
        runOnUiThread {
            val loading = findViewById<GifImageView>(R.id.loadingInfo)
            loading.visibility = View.INVISIBLE
            myList = ArrayList()
            if (products.isNotEmpty()) {
                for (product in products) {
                    if (!product.inputProductLots[componentDad].isNullOrEmpty()) {
                        myList.add(
                            ListAssoComp(
                                product.outputProductLot.referenceNumber,
                                product.outputProductLot.designation,
                                product.outputProductLot.productType
                            )
                        )
                    }
                }
                listAssoComp.adapter = ListAssoCompAdapter(myList)
                listAssoComp.layoutManager = LinearLayoutManager(this@InfoComponent)


            }
            if (myList.isEmpty()) {
                noInfoAssociatedComponents.visibility = View.VISIBLE
            } else {
                noInfoAssociatedComponents.visibility = View.INVISIBLE
                val adapter = ListAssoCompAdapter(myList)
                listAssoComp.adapter = adapter
                adapter.setOnItemClickListener(object :
                    ListAssoCompAdapter.onItemClickListener {
                    override fun onItemClick(idComp: TextView) {
                        finish();
                        val intent = intent.apply {
                            putExtra(PARAM_Reference_Number, idComp.text.toString())
                        }
                        startActivity(intent)
                    }
                })
                listAssoComp.layoutManager = LinearLayoutManager(this@InfoComponent)
            }
        }

    }


    suspend fun getComponent(referenceNumber: String): String {
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
                    createPopUp(getString(R.string.invalid_token))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                canGO = true
                Toast.makeText(this@InfoComponent, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        while (!canGO) {
            delay(1)
        }
        return product.ID
    }


    fun createPopUp(msg: String) {
        val dialog = Dialog(this@InfoComponent)
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
            val intent = Intent(this@InfoComponent, MainActivity::class.java)
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
                val intent = Intent(this@InfoComponent, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@InfoComponent, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@InfoComponent, ListComponents::class.java)
                startActivity(intent)
                true
            }
//            R.id.nav_pending -> {
//                val intent = Intent(this@InfoComponent, ListPending::class.java)
//                startActivity(intent)
//                true
//            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun sendToAdd(view: View) {
        val refNum = findViewById<TextView>(R.id.valueReferenceNumberInfoComponent).text.toString()
        val intent = Intent(this@InfoComponent, AddComponent::class.java).apply {
            putExtra(PARAM_Reference_Number_PARENT, refNum)
        }
        startActivity(intent)
    }
}