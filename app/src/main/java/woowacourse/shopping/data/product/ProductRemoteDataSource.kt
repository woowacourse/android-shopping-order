package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.product.dto.ProductDetail
import woowacourse.shopping.data.product.dto.ProductListInfo

class ProductRemoteDataSource(
    baseUrl: String,
    private val userId: String,
) : ProductDataSource {

    private val retrofitService: ProductsRetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductsRetrofitService::class.java)

    override fun findProductById(id: Long, callback: (ProductDetail) -> Unit) {
        retrofitService.getProductDetail(userId, id)
            .enqueue(
                object : retrofit2.Callback<ProductDetail> {
                    override fun onResponse(
                        call: Call<ProductDetail>,
                        response: Response<ProductDetail>,
                    ) {
                        val productDetails = response.body()
                        if (productDetails != null) {
                            callback(productDetails)
                        }
                    }

                    override fun onFailure(call: Call<ProductDetail>, t: Throwable) {}
                },
            )
    }

    override fun getProductsWithRange(
        lastId: Long,
        pageItemCount: Int,
        callback: (List<ProductDetail>, Boolean) -> Unit,
    ) {
        retrofitService.getProductDetails(userId, lastId, pageItemCount)
            .enqueue(
                object : retrofit2.Callback<ProductListInfo> {
                    override fun onResponse(
                        call: Call<ProductListInfo>,
                        response: Response<ProductListInfo>,
                    ) {
                        val productDetails = response.body()?.products ?: listOf()
                        val isLast = response.body()?.last ?: true
                        callback(productDetails, isLast)
                    }

                    override fun onFailure(call: Call<ProductListInfo>, t: Throwable) {}
                },
            )
    }
}
