package woowacourse.shopping.data.product

import retrofit2.Call

interface ProductRemoteDataSource {
    fun getProductById(id: Int): Call<ProductDataModel>
    fun getAllProducts(): Call<List<ProductDataModel>>
}
