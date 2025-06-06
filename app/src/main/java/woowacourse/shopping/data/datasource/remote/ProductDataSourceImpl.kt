package woowacourse.shopping.data.datasource.remote

import retrofit2.HttpException
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.remote.ProductService

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): List<ProductContent> {
        val response = productService.requestProducts(page, pageSize, category)
        if (response.isSuccessful) {
            return response.body()?.content ?: emptyList()
        }
        throw HttpException(response)
    }

    override suspend fun fetchProductById(id: Long): ProductContent {
        val response = productService.requestProductById(id)
        if (response.isSuccessful) {
            return response.body() ?: throw NoSuchElementException(ERROR_NOT_FOUND_PRODUCT)
        }
        throw HttpException(response)
    }

    companion object {
        private const val ERROR_NOT_FOUND_PRODUCT = "해당 id의 상품을 찾지 못했습니다."
    }
}
