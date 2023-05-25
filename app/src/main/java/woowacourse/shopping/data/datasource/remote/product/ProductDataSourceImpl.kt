package woowacourse.shopping.data.datasource.remote.product

import okhttp3.Call
import woowacourse.shopping.data.remote.NetworkModule

class ProductDataSourceImpl : ProductDataSource {

    override fun getAllProducts(): Call {
        // val request = Request.Builder().url("http://3.34.134.115:8080/products").build()

        return NetworkModule.getService(GET_PRODUCT_PATH)
    }

    companion object {
        private const val GET_PRODUCT_PATH = "/products"
    }
}
