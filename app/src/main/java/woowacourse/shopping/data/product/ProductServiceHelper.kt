package woowacourse.shopping.data.product

import ProductService
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProductServiceHelper : ProductRemoteDataSource {
    var baseUrl = ""

    private val retrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductService::class.java)

    override fun getProductById(id: Int): Call<ProductDataModel> {
        return retrofitService.getProductById(id)
    }

    override fun getAllProducts(): Call<List<ProductDataModel>> {
        return retrofitService.getAllProducts()
    }
}
