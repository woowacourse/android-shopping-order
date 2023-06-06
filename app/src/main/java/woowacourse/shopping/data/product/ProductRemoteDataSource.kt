package woowacourse.shopping.data.product

import retrofit2.Retrofit
import woowacourse.shopping.data.product.dto.ProductDetail
import woowacourse.shopping.data.product.dto.ProductListInfo
import woowacourse.shopping.data.util.RetrofitCallback

class ProductRemoteDataSource(
    retrofit: Retrofit,
) : ProductDataSource {

    private val retrofitService = retrofit.create(ProductsRetrofitService::class.java)

    override fun findProductById(id: Long, callback: (ProductDetail) -> Unit) {
        retrofitService.getProductDetail(id)
            .enqueue(
                object : RetrofitCallback<ProductDetail>() {
                    override fun onSuccess(response: ProductDetail?) {
                        if (response != null) callback(response)
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
                object : RetrofitCallback<ProductListInfo>() {
                    override fun onSuccess(response: ProductListInfo?) {
                        val productDetails = response?.products ?: listOf()
                        callback(productDetails, response?.last ?: true)
                    }
                },
            )
    }
}
