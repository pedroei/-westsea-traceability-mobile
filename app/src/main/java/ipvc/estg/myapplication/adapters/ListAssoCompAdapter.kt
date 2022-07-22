package ipvc.estg.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.models.ListAssoComp
import kotlinx.android.synthetic.main.list_associated_component_line.view.*


class ListAssoCompAdapter(
    private val linhas: ArrayList<ListAssoComp>
) : RecyclerView.Adapter<ListAssoCompViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(idComp: TextView)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAssoCompViewHolder {
        return ListAssoCompViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.list_associated_component_line,
                    parent,
                    false
                ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: ListAssoCompViewHolder, position: Int) {
        val currentLinha = linhas[position]

        holder.idlistAssociatedComp.text = currentLinha.idlistAssociatedComp
        holder.namelistAssociatedComp.text = currentLinha.namelistAssociatedComp
        holder.materiallistAssociatedComp.text = currentLinha.materiallistAssociatedComp
    }

    override fun getItemCount(): Int {
        return linhas.size
    }
}

class ListAssoCompViewHolder(
    itemView: View,
    listener: ListAssoCompAdapter.onItemClickListener
) : RecyclerView.ViewHolder(itemView) {
    val idlistAssociatedComp: TextView = itemView.idlistAssociatedComp
    val namelistAssociatedComp: TextView = itemView.namelistAssociatedComp
    val materiallistAssociatedComp: TextView = itemView.materiallistAssociatedComp
    init {
        itemView.setOnClickListener{
            listener.onItemClick(idlistAssociatedComp)
        }
    }
}