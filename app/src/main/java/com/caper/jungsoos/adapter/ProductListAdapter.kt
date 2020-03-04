package com.caper.jungsoos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caper.jungsoos.R
import com.caper.jungsoos.entities.Cart
import com.caper.jungsoos.entities.Product

/**
 * This is the Product List Adapter for the Carts
 */
class ProductListAdapter(private var mContext: Context) :
    ListAdapter<Cart, ProductListAdapter.ProductHolder>(DIFF_CALLBACK) {
    private var listenerSpinner: OnItemSelectedListener? = null
    private var listener: OnItemClickListener? = null

    /**
     * View Holder for the Item
     */
    inner class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        var tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        var spProductQuantity: Spinner = itemView.findViewById(R.id.spProductQuntity)
        val ibProductRemove: ImageButton = itemView.findViewById(R.id.ibProductRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val currentProduct = getItem(position)
        Glide.with(mContext)
            .load(currentProduct.product?.thumbnail)
            .into(holder.ivProduct)
        holder.tvProductName.text = currentProduct.product?.name
        holder.tvProductPrice.text = currentProduct.product?.price
        holder.spProductQuantity.setSelection(
            (holder.spProductQuantity.adapter as ArrayAdapter<String>).getPosition(
                currentProduct.quantity.toString()
            )
        )
        /**
         * Call Back function to update the View Model if quantity value is changed
         */
        holder.spProductQuantity.onItemSelectedListener = object : OnItemSelectedListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelectedListener(quantity: String?, product: Product?) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listenerSpinner!!.onItemSelectedListener(
                    p0?.getItemAtPosition(p2).toString(),
                    currentProduct.product
                )
            }
        }

        /**
         * Call Back function to Remove product from the List, This will update the View Model and View Model will update the List
         */
        holder.ibProductRemove.setOnClickListener {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener!!.onItemClick(currentProduct.product?.id)
            }
        }
    }

    /**
     * This is to check the List have same Item but in this app we are using View Model to check we have same set of Product
     */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.product?.id == newItem.product?.id
            }

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.product?.id == newItem.product?.id
            }
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelectedListener(quantity: String?, product: Product?)
    }

    fun onItemSelectedListener(listenerSpinner: OnItemSelectedListener) {
        this.listenerSpinner = listenerSpinner
    }

    interface OnItemClickListener {
        fun onItemClick(itemID: String?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}