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
        val retrofitCallback = object : RetrofitCallback<ProductDetail>() {
            override fun onSuccess(response: ProductDetail?) {
                if (response != null) callback(response)
            }
        }
        retrofitService.getProductDetail(id).enqueue(retrofitCallback)
    }

    override fun getProductsWithRange(
        lastId: Long,
        pageItemCount: Int,
        callback: (List<ProductDetail>, Boolean) -> Unit,
    ) {
        val retrofitCallback = object : RetrofitCallback<ProductListInfo>() {
            override fun onSuccess(response: ProductListInfo?) {
                val productDetails = response?.products ?: listOf()
                callback(productDetails, response?.last ?: true)
            }
        }
        retrofitService.getProductDetails(lastId, pageItemCount).enqueue(retrofitCallback)
    }
}
