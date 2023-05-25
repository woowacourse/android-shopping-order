package woowacourse.shopping.data.datasource.remote.product

import okhttp3.Call
import woowacourse.shopping.data.remote.NetworkModule

class ProductDataSourceImpl : ProductDataSource {

    override fun getAllProducts(): Call {
        return NetworkModule.callRequest(GET_PRODUCT_PATH)
    }

    companion object {
        private const val GET_PRODUCT_PATH = "/products"
    }
}
