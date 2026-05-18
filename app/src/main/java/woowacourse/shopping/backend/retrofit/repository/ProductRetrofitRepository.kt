package woowacourse.shopping.backend.retrofit.repository

import woowacourse.shopping.backend.retrofit.api.ProductRetrofit
import woowacourse.shopping.backend.retrofit.bodyOrThrow
import woowacourse.shopping.backend.retrofit.throwOnFailure
import woowacourse.shopping.backend.retrofit.dto.Product
import woowacourse.shopping.backend.retrofit.dto.ProductResponse

class ProductRetrofitRepository(
    private val apiService: ProductRetrofit,
) {
    suspend fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = null,
        category: String? = null,
    ): ProductResponse =
        apiService.requestProducts(
            page = page,
            size = size,
            sort = sort,
            category = category,
        ).bodyOrThrow(errorPrefix = "상품 조회 실패")

    suspend fun requestProductDetail(id: Long): Product =
        apiService.requestProductDetail(
            id = id,
        ).bodyOrThrow(errorPrefix = "상품 조회 실패")

    suspend fun addProduct(product: Product) {
        apiService.addProduct(
            product = product,
        ).throwOnFailure(errorPrefix = "상품 추가 실패")
    }

    suspend fun deleteProduct(id: Long) {
        apiService.deleteProduct(
            id = id,
        ).throwOnFailure(errorPrefix = "상품 삭제 실패")
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
    }
}
