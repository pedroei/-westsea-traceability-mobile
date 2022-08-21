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
import ipvc.estg.myapplication.MainActivity
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.api.APIService
import ipvc.estg.myapplication.api.ServiceBuilder
import ipvc.estg.myapplication.models.Product
import kotlinx.android.synthetic.main.dialog_pdf_viewer.*
import kotlinx.android.synthetic.main.pop_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssociatedDocuments : AppCompatActivity() {

    lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_associated_documents)

        GlobalScope.launch {
            val referenceNumber = intent.getStringExtra(REFERENCE_NUMBER_PARAM)
            getProduct(referenceNumber.toString())
        }
    }

    private suspend fun getProduct(referenceNumber: String) {

        val loading = findViewById<GifImageView>(R.id.loadingInfo)
        runOnUiThread {
            loading.visibility = View.VISIBLE
        }

        var canGO = false

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
                    product = response.body()!!
                        .filter { it.referenceNumber == referenceNumber }[0]

                    GlobalScope.launch {
                        //FIXME: for now only shows the first, implemment all
//                        showPdf(product)
                    }

                    canGO = true
                } else {
                    createPopUp(getString(R.string.invalid_token))
                    canGO = true
                }

            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                canGO = true
                Toast.makeText(this@AssociatedDocuments, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        while (!canGO) {
            delay(1)
        }
        val valueNameTitleInfoComponent = findViewById<TextView>(R.id.valueNameTitleInfoComponent)

        runOnUiThread {
            valueNameTitleInfoComponent.text = product.designation
            loading.visibility = View.INVISIBLE
        }
    }

    private suspend fun showPdf(prod: Product) {
        if (prod.documentKeys.isNotEmpty()) {
            var canGO = false
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("tokenSP", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            val request = ServiceBuilder.buildService(APIService::class.java)
            val call = request.downloadDocument(
                "Bearer $token",
                prod.ID,
                prod.documentKeys.get(0).documentKey
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val byteArray = response.body()!!.bytes()
                        createDialogHelp(byteArray)

                        canGO = true
                    } else {
                        createPopUp(getString(R.string.invalid_token))
                        canGO = true
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    canGO = true
                    Toast.makeText(
                        this@AssociatedDocuments,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
            while (!canGO) {
                delay(1)
            }
        }

    }

    private fun createDialogHelp(fileBytes: ByteArray) {
        val dialog = Dialog(this@AssociatedDocuments)
        dialog.setContentView(R.layout.dialog_pdf_viewer)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<TextView>(R.id.iconClosePdfViewer).setOnClickListener {
            dialog.dismiss()
        }

        dialog.pdfView.fromBytes(fileBytes).load()

    }

    fun createPopUp(msg: String) {
        val dialog = Dialog(this@AssociatedDocuments)
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
            val intent = Intent(this@AssociatedDocuments, MainActivity::class.java)
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
                val intent = Intent(this@AssociatedDocuments, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@AssociatedDocuments, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@AssociatedDocuments, ListComponents::class.java)
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
}