package com.caper.jungsoos

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caper.jungsoos.adapter.ProductFilterAdapter
import com.caper.jungsoos.database.AppDatabase
import com.caper.jungsoos.entities.Cart
import com.caper.jungsoos.entities.Product
import com.caper.jungsoos.entities.ProductList
import com.caper.jungsoos.viewmodel.CartListViewModel
import com.caper.jungsoos.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_search.*

/**
 * This is the Search Activity. User can Search Product. This will show the product as a List and User can lookup using the search view.
 * Version - V 1.0.0
 * If we have more item we need to fetch first 20,30 Items to show in list on user scrolls fetch next list of Items
 */

class SearchActivity : AppCompatActivity() {

    private var mDb: AppDatabase? = null
    private lateinit var vmCartList: CartListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val intent = Intent(this, MainActivity::class.java)

        /**
         * SqlLite Initialization
         */
        if (mDb == null) {
            mDb = AppDatabase.getInstance(applicationContext)
        }
        /**
         * Cart View Model initialization
         */
        vmCartList = this.run {
            ViewModelProvider(
                this,
                ViewModelFactory.getInstance()
            ).get(CartListViewModel::class.java)
        }

        /**
         * Fetching Product List from SqlLite
         */
        val productList = mDb?.productModel()?.getProduct()

        /**
         * Setting Product Adapter to show Products
         */
        val productFilterAdapter: ProductFilterAdapter? = ProductFilterAdapter(productList, this)

        /**
         * Setting Recycler view and Load Products.
         */
        rvProductList?.layoutManager = LinearLayoutManager(this)
        rvProductList?.setHasFixedSize(true)
        rvProductList?.itemAnimator = DefaultItemAnimator()
        rvProductList?.adapter =
            productFilterAdapter
        rvProductList?.addItemDecoration(
            DividerItemDecoration(
                rvProductList?.context,
                DividerItemDecoration.VERTICAL
            )
        )

        /**
         * Call Back function on user clicks in the product List
         */
        productFilterAdapter?.setOnItemClickListener(object :
            ProductFilterAdapter.OnItemClickListener {
            override fun onItemClick(product: Product?) {
                val quantity: Int? =
                    if ((vmCartList.cartList.value?.get(product?.id)?.quantity) == null) 0 else vmCartList.cartList.value?.get(
                        product?.id
                    )?.quantity
                if (vmCartList.cartList.value?.values == null) {
                    val cartValue = Cart(product, 1)
                    vmCartList.cartList.value = linkedMapOf(product!!.id to cartValue)
                } else {
                    val cartValue = Cart(product, quantity?.plus(1))
                    vmCartList.cartList.value?.put(
                        product!!.id,
                        cartValue
                    )
                }
                startActivity(intent)
            }
        })

        /**
         * This is the search view Filter function. Filtration based on the Text. Implemented in the Adapter
         */
        svProductFilter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                productFilterAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                productFilterAdapter?.filter?.filter(newText)
                return false
            }
        })
    }
}
