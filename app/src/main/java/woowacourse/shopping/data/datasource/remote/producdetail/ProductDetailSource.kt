package woowacourse.shopping.data.datasource.remote.producdetail

import okhttp3.Call

interface ProductDetailSource {
    fun getById(id: Long): Call
}
