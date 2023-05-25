package woowacourse.shopping.data.datasource.remote.product

import okhttp3.Call

interface ProductDataSource {

    fun getAllProducts(): Call
}
