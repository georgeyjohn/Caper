package com.caper.jungsoos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caper.jungsoos.R
import com.caper.jungsoos.entities.Product
import java.util.*

/**
 * This is the Product Adapter
 * This adapter helps to show the Product list and to filter Product form the List  using Search View
 */
class ProductFilterAdapter(private val product: List<Product>?, private val mContext: Context) :
    RecyclerView.Adapter<ProductFilterAdapter.ViewHolder>(), Filterable {

    var productFilteredList: List<Product>? = product
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.item_product_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productFilteredList?.size!!
    }

    /**
     * View Holder for the Item
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProduct: TextView = view.findViewById(R.id.tvProduct)
        val ivProduct: ImageView = view.findViewById(R.id.ivProduct)
        val clItem: ConstraintLayout = view.findViewById(R.id.clProductFilter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productFilteredList?.get(position)
        holder.tvProduct.text = item?.name
        /**
         * Glide is to help load image - Glide is a library
         */
        Glide.with(mContext)
            .load(item?.thumbnail)
            .into(holder.ivProduct)

        /**
         * Call Back function for the product click, This will update the View Model Cart
         */
        holder.clItem.setOnClickListener {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener!!.onItemClick(item)
            }
        }
    }

    /**
     * This is the filter function for the Recycler View.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                /**
                 * This will compare the value and update this Recycler view Product List
                 */
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    productFilteredList = product
                } else {
                    val filteredList: MutableList<Product> =
                        ArrayList()
                    for (row in product!!) {
                        if (row.name.toLowerCase(Locale.getDefault()).contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            ) || row.name.contains(charSequence)
                        ) {
                            filteredList.add(row)
                        }
                    }
                    productFilteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = productFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                productFilteredList = filterResults.values as List<Product>?
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}


