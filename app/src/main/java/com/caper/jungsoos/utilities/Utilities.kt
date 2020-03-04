package com.caper.jungsoos.utilities

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.caper.jungsoos.entities.Product
import com.caper.jungsoos.viewmodel.CartListViewModel
import com.caper.jungsoos.viewmodel.ViewModelFactory
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8


/**
 * This Class is to write General Methods for the Application.
 **/

class Utilities {

    companion object {
        private const val PRODUCT_LIST = "product_list.json"
        /**
         * This method is to load JSON File from the Asset and returns the JSON String
         * @param context is the Application context
         */
        fun loadJSONFromAsset(context: Context): String? {
            val json: String?
            json = try {
                val `is`: InputStream = context.assets.open(PRODUCT_LIST)
                val size: Int = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }
            return json
        }
    }

}