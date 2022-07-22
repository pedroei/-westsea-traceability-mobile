package ipvc.estg.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.models.ListProducts
import kotlinx.android.synthetic.main.list_line.view.*


class ListAdapter(
    private val linhas: ArrayList<ListProducts>
) : RecyclerView.Adapter<ListViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(idComp: TextView)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.list_line,
                    parent,
                    false
                ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentLinha = linhas[position]

        holder.nomeComp.text = currentLinha.nomeComp
        holder.idComp.text = currentLinha.idComp
        holder.material.text = currentLinha.material
    }

    override fun getItemCount(): Int {
        return linhas.size
    }
}

class ListViewHolder(
    itemView: View,
    listener: ListAdapter.onItemClickListener
) : RecyclerView.ViewHolder(itemView) {
    val nomeComp: TextView = itemView.nomeComp
    val idComp: TextView = itemView.idComp
    val material: TextView = itemView.material
    init {
        itemView.setOnClickListener{
            listener.onItemClick(idComp)
        }
    }
}