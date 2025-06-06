package woowacourse.shopping.data.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService

class CatalogRemoteDataSourceImpl(
    private val productService: ProductService,
) : CatalogRemoteDataSource {
    override suspend fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): ProductResponse =
        withContext(Dispatchers.IO) {
            val response =
                productService.requestProducts(category = category, page = page, size = size)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("추천 상품이 없습니다.")
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }

    override suspend fun fetchAllProducts(): ProductResponse =
        withContext(Dispatchers.IO) {
            val response = productService.requestProducts()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("전체 상품이 없습니다.")
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }

    override suspend fun fetchProductDetail(id: Long): ProductContent =
        withContext(Dispatchers.IO) {
            val response = productService.requestDetailProduct(id)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("상품 상세가 없습니다.")
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }
}
