package woowacourse.shopping.data.remote.product

import retrofit2.HttpException

class ProductRepository(
    private val productService: ProductService,
) {
    suspend fun fetchProducts(page: Int): Result<ProductResponse?> =
        runCatching {
            val response = productService.requestGoods(page = page)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }

    suspend fun fetchAllProducts(): Result<ProductResponse?> =
        runCatching {
            val response = productService.requestGoods(size = Int.MAX_VALUE)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }

    suspend fun requestProductDetails(productId: Long): Result<ProductDetailResponse?> =
        runCatching {
            val response = productService.requestProductDetails(productId = productId)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }
}
