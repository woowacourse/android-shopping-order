package woowacourse.shopping.data.remote.product

class ProductRepository(
    private val productService: ProductService,
) {
    suspend fun fetchProducts(page: Int): Result<ProductResponse?> =
        try {
            val response = productService.requestGoods(page = page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun fetchAllProducts(): Result<ProductResponse?> =
        try {
            val response = productService.requestGoods(size = Int.MAX_VALUE)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun requestProductDetails(productId: Long): Result<ProductDetailResponse?> =
        try {
            val response = productService.requestProductDetails(productId = productId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
