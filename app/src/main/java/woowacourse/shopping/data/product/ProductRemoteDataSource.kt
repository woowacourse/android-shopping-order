package woowacourse.shopping.data.product

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.product.dto.ProductDetail
import woowacourse.shopping.data.product.dto.ProductListInfo

class ProductRemoteDataSource(
    retrofit: Retrofit,
) : ProductDataSource {

    private val retrofitService = retrofit.create(ProductsRetrofitService::class.java)

    override fun findProductById(id: Long, callback: (ProductDetail) -> Unit) {
        retrofitService.getProductDetail(id)
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

                    override fun onFailure(call: Call<ProductDetail>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun getProductsWithRange(
        lastId: Long,
        pageItemCount: Int,
        callback: (List<ProductDetail>, Boolean) -> Unit,
    ) {
        retrofitService.getProductDetails(lastId, pageItemCount)
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

                    override fun onFailure(call: Call<ProductListInfo>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }
}
