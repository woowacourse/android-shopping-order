package woowacourse.shopping.data.datasource.product.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.data.remote.OkHttpModule
import java.lang.Integer.min

class RemoteProductDataSource : ProductDataSource.Remote {

    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<DataProduct>) -> Unit,
    ) {
        // todo: url 관리방법 변경해야함
        val url = OkHttpModule.BASE_URL

        val retrofitService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductService::class.java)

        retrofitService.requestProducts().enqueue(object : retrofit2.Callback<List<DataProduct>> {

            override fun onResponse(
                call: retrofit2.Call<List<DataProduct>>,
                response: retrofit2.Response<List<DataProduct>>,
            ) {
                response.body()?.let {
                    onReceived(
                        getDataProductsFromCache(
                            size = size,
                            lastId = lastId,
                            allProducts = it
                        )
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<List<DataProduct>>, t: Throwable) {
            }
        })
    }

    private fun getDataProductsFromCache(
        size: Int,
        lastId: Int,
        allProducts: List<DataProduct>,
    ): List<DataProduct> {
        if (lastId == -1) return allProducts.subList(0, min(allProducts.size, size))
        val startIndex = allProducts.indexOfFirst { it.id == lastId } + 1
        return allProducts.subList(startIndex, min(allProducts.size, startIndex + size))
    }
}
