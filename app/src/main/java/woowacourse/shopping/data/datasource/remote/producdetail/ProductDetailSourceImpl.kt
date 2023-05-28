package woowacourse.shopping.data.datasource.remote.producdetail

import okhttp3.Call
import woowacourse.shopping.data.remote.NetworkModule

class ProductDetailSourceImpl : ProductDetailSource {
    override fun getById(id: Long): Call {
        return NetworkModule.getService(GET_PRODUCT_DETAIL_PATH + id)
    }
    
    companion object {
        private const val GET_PRODUCT_DETAIL_PATH = "/products/"
    }
}
