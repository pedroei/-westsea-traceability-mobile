package ipvc.estg.myapplication.aplication

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.myapplication.ComponenteApplication
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.adapters.ListPendingAdapter
import ipvc.estg.myapplication.models.Componente
import ipvc.estg.myapplication.viewModel.ComponenteViewModel
import ipvc.estg.myapplication.viewModel.ComponenteViewModelFactory
import kotlinx.android.synthetic.main.activity_list_pending.*


class ListPending : AppCompatActivity() {

    private val componenteViewModel: ComponenteViewModel by viewModels {
        ComponenteViewModelFactory((application as ComponenteApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_pending)

        var activitiesArray: Array<String> = emptyArray()

        activitiesArray = activitiesArray.plus(getString(R.string.search_rn))
        activitiesArray = activitiesArray.plus(getString(R.string.search_designation))
        activitiesArray = activitiesArray.plus(getString(R.string.search_type))

        spinnerDown(activitiesArray)

        getProducts()

        rnListPending.addTextChangedListener {
            getProductsSearch()
        }

        btnHelpListCompPending.setOnClickListener {
            createDialogHelp()
        }
    }

    private fun getProductsSearch() {
        val filtro = findViewById<Spinner>(R.id.filterListPending).selectedItem.toString()

        // recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.listLinePending)
        val adapter = ListPendingAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        componenteViewModel.allComponents.observe(this, Observer { comp ->
            // Update the cached copy of the words in the adapter.
            comp?.let {
                if (it.isEmpty()) {
                    Log.d("VALE", "EMPTY")
                } else {
                    val adapter = ListPendingAdapter()
                    if (filtro == getString(R.string.search_rn)) {
                        val listaPendentes = mutableListOf<Componente>()
                        for (componente in it) {
                            if (componente.referenceNumber.take(rnListPending.text.length) == rnListPending.text.toString()) {
                                listaPendentes.add(componente)
                            }
                        }
                        adapter.submitList(listaPendentes)
                        listLinePending.adapter = adapter
                        adapter.setOnItemClickListener(object :
                            ListPendingAdapter.onItemClickListener {
                            override fun onItemClick(idComp: TextView) {
                                val intent =
                                    Intent(
                                        this@ListPending,
                                        InfoComponentPending::class.java
                                    ).apply {
                                        putExtra(
                                            PARAM_Reference_Number,
                                            idComp.text.toString()
                                        )
                                    }
                                startActivity(intent)
                            }
                        })
                        listLinePending.layoutManager =
                            LinearLayoutManager(this@ListPending)
                    }else if (filtro == getString(R.string.search_designation)) {
                        val listaPendentes = mutableListOf<Componente>()
                        for (componente in it) {
                            if (componente.designation.take(rnListPending.text.length) == rnListPending.text.toString()) {
                                listaPendentes.add(componente)
                            }
                        }
                        adapter.submitList(listaPendentes)
                        listLinePending.adapter = adapter
                        adapter.setOnItemClickListener(object :
                            ListPendingAdapter.onItemClickListener {
                            override fun onItemClick(idComp: TextView) {
                                val intent =
                                    Intent(
                                        this@ListPending,
                                        InfoComponentPending::class.java
                                    ).apply {
                                        putExtra(
                                            PARAM_Reference_Number,
                                            idComp.text.toString()
                                        )
                                    }
                                startActivity(intent)
                            }
                        })
                        listLinePending.layoutManager =
                            LinearLayoutManager(this@ListPending)
                    }else if (filtro == getString(R.string.search_type)) {
                        val listaPendentes = mutableListOf<Componente>()
                        for (componente in it) {
                            if (componente.productType.take(rnListPending.text.length) == rnListPending.text.toString()) {
                                listaPendentes.add(componente)
                            }
                        }
                        adapter.submitList(listaPendentes)
                        listLinePending.adapter = adapter
                        adapter.setOnItemClickListener(object :
                            ListPendingAdapter.onItemClickListener {
                            override fun onItemClick(idComp: TextView) {
                                val intent =
                                    Intent(
                                        this@ListPending,
                                        InfoComponentPending::class.java
                                    ).apply {
                                        putExtra(
                                            PARAM_Reference_Number,
                                            idComp.text.toString()
                                        )
                                    }
                                startActivity(intent)
                            }
                        })
                        listLinePending.layoutManager =
                            LinearLayoutManager(this@ListPending)
                    }
                }


            }
        })
    }

    fun getProducts() {
        // recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.listLinePending)
        val adapter = ListPendingAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        componenteViewModel.allComponents.observe(this, Observer { comp ->
            // Update the cached copy of the words in the adapter.
            comp?.let {
                if (it.isEmpty()) {
                    Log.d("VALE", "EMPTY")
                } else {
                    val adapter = ListPendingAdapter()
                    adapter.submitList(it)
                    listLinePending.adapter = adapter
                    adapter.setOnItemClickListener(object :
                        ListPendingAdapter.onItemClickListener {
                        override fun onItemClick(idComp: TextView) {
                            val intent =
                                Intent(this@ListPending, InfoComponentPending::class.java).apply {
                                    putExtra(PARAM_Reference_Number, idComp.text.toString())
                                }
                            startActivity(intent)
                        }
                    })
                    listLinePending.layoutManager = LinearLayoutManager(this@ListPending)
                }


            }
        })
    }

    fun spinnerDown(activitiesArray: Array<String>) {
        val spinner = findViewById<Spinner>(R.id.filterListPending)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.bottom_navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_scan -> {
                val intent = Intent(this@ListPending, ScanPage::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_add -> {
                val intent = Intent(this@ListPending, AddComponent::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_list -> {
                val intent = Intent(this@ListPending, ListComponents::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_pending -> {
                true
            }
            else -> {
                Log.d("teste", "entrou")
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun createDialogHelp() {
        val dialog = Dialog(this@ListPending)
        dialog.setContentView(R.layout.dialog_list_components_pending)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.pop_up_background))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<TextView>(R.id.iconClosePopUpListComponentsPending).setOnClickListener {
            dialog.dismiss()
        }

    }
}





