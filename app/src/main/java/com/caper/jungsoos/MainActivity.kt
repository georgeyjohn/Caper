package com.caper.jungsoos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caper.jungsoos.adapter.ProductListAdapter
import com.caper.jungsoos.database.AppDatabase
import com.caper.jungsoos.entities.Cart
import com.caper.jungsoos.entities.Product
import com.caper.jungsoos.utilities.Utilities
import com.caper.jungsoos.viewmodel.CartListViewModel
import com.caper.jungsoos.viewmodel.ProductViewModel
import com.caper.jungsoos.viewmodel.ViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Type

/**
 * This is the Main Activity. User can see the cart from this Activity user can Add Product and Scan barcode.
 * Version - V 1.0.0
 */


class MainActivity : AppCompatActivity() {
    private lateinit var vmProduct: ProductViewModel
    private lateinit var vmCartList: CartListViewModel
    var productAdapter: ProductListAdapter? = null

    private var mDb: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val productListString = Utilities.loadJSONFromAsset(applicationContext)
        productAdapter = ProductListAdapter(this)
        if (mDb == null) {
            mDb = AppDatabase.getInstance(applicationContext)
        }
        /**
         * View Model for the Cart, This have the product and quantity details
         */
        vmCartList = this.run {
            ViewModelProvider(
                this,
                ViewModelFactory.getInstance()
            ).get(CartListViewModel::class.java)
        }

        /**
         * Barcode Scanner
         */
        ivBarcodeScanner.setOnClickListener {
            IntentIntegrator(this).initiateScan();
        }
        /**
         * This is the product View Model, This have Product details (We don't need to use this, I'm showing save data to to SqlLite using View Model)
         */
        vmProduct = this.run {
            ViewModelProvider(this).get(ProductViewModel::class.java)
        }

        /**
         * Taking the JSON string from asset folder and converting to Array of Products
         */
        val type: Type = object : TypeToken<List<Product?>?>() {}.type
        val productList: ArrayList<Product> = Gson().fromJson(productListString, type)
        vmProduct.productList.value = productList
        /**
         * Inserting Product to SqlLite
         */
        mDb?.productModel()?.insertProduct(productList)

        /**
         * Setting the recycler View
         */
        rvProductList?.layoutManager = LinearLayoutManager(this)
        rvProductList?.setHasFixedSize(true)
        rvProductList?.itemAnimator = DefaultItemAnimator()
        rvProductList?.adapter = productAdapter
        productAdapter?.submitList(vmCartList.cartList.value?.values?.toList())
        rvProductList?.addItemDecoration(
            DividerItemDecoration(
                rvProductList?.context,
                DividerItemDecoration.VERTICAL
            )
        )
        /**
         * This is a callback from recycler view Spinner to Update the product quantity
         */
        productAdapter?.onItemSelectedListener(object : ProductListAdapter.OnItemSelectedListener {
            override fun onItemSelectedListener(quantity: String?, product: Product?) {
                val cartValue = Cart(product, quantity?.toInt())
                vmCartList.cartList.value?.put(
                    product!!.id,
                    cartValue
                )
                totalProduct()
            }

        })

        /**
         * This is a call back from recycler view for the delete button click, This remove product from the cart list
         */
        productAdapter?.setOnItemClickListener(object : ProductListAdapter.OnItemClickListener {
            override fun onItemClick(itemID: String?) {
                vmCartList.cartList.value?.remove(itemID)
                productAdapter?.submitList(vmCartList.cartList.value?.values?.toList())
                totalProduct()
            }
        })

        /**
         * This is the floating Button click. This will go to Search Product Activity where user can search Products to add products to carts
         */
        fabAddProduct.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        vmCartList.cartList.observe(this, Observer {
            totalProduct()
        })
    }

    /**
     * This is a private function to find the total price of the item and this will update the Total value in UI
     */
    @SuppressLint("SetTextI18n")
    private fun totalProduct() {
        val list = vmCartList.cartList.value?.values
        var total: Double? = 0.0
        list?.forEach { item ->
            run {
                total = (item.product?.price!!.replace(
                    "$",
                    ""
                ).toDouble() * item.quantity!!.toDouble()).plus(total!!)
            }
        }
        tvTotal.text = "$ ${total.toString()}"
    }

    /**
     * This is an Activity Result from the Barcode scanner
     * This will search for the product and will add to cart if product not found show a 'Cannot find the Product' Message
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            /**
             * Barcode back Button press
             */
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                /**
                 * Updating the cart using barcode value
                 * This will call the Sql query with param{Barcode} value
                  */
                val product = mDb?.productModel()?.getProductWithID(result.contents)
                if (product != null) {
                    val quantity: Int? =
                        if ((vmCartList.cartList.value?.get(product.id)?.quantity) == null) 0 else vmCartList.cartList.value?.get(
                            product.id
                        )?.quantity
                    if (vmCartList.cartList.value?.values == null) {
                        val cartValue = Cart(product, 1)
                        vmCartList.cartList.value = linkedMapOf(product.id to cartValue)
                    } else {
                        val cartValue = Cart(product, quantity?.plus(1))
                        vmCartList.cartList.value?.put(
                            product.id,
                            cartValue
                        )
                    }
                    productAdapter?.submitList(vmCartList.cartList.value?.values?.toList())
                    productAdapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Cannot find the Product", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}
