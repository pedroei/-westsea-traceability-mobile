package ipvc.estg.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.myapplication.R
import ipvc.estg.myapplication.models.Componente


class ListPendingAdapter :
    ListAdapter<Componente, ListPendingAdapter.ComponenteViewHolder>(ComponenteComparator()) {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(idComp: TextView)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponenteViewHolder {
        return ComponenteViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.list_pending_line,
                    parent,
                    false
                ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: ComponenteViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }


    class ComponenteViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val componenteItemView: TextView = itemView.findViewById(R.id.nomeCompPending)
        private val componenteItemView2: TextView = itemView.findViewById(R.id.idCompPending)
        private val componenteItemView3: TextView = itemView.findViewById(R.id.materialPending)

        fun bind(current: Componente) {
            componenteItemView.text = current.designation
            componenteItemView2.text = current.referenceNumber
            componenteItemView3.text = current.productType
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(componenteItemView2)
            }
        }
    }

    class ComponenteComparator : DiffUtil.ItemCallback<Componente>() {
        override fun areItemsTheSame(oldItem: Componente, newItem: Componente): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Componente, newItem: Componente): Boolean {
            return oldItem.designation == newItem.designation
        }
    }
}