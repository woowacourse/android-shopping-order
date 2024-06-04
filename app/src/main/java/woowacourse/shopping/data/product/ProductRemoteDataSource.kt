package woowacourse.shopping.data.product

import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductsApiService

class ProductRemoteDataSource(
    private val productsApiService: ProductsApiService,
) : ProductDataSource {
    override fun findByPaged(page: Int): List<ProductDto> =
        productsApiService.requestProducts(page = page).execute().body()?.content
            ?: throw NoSuchElementException("there is no product with page: $page")

    override fun findById(id: Long): ProductDto =
        productsApiService.requestProduct(id.toInt()).execute().body()
            ?: throw NoSuchElementException("there is no product with id: $id")

    override fun isFinalPage(page: Int): Boolean {
        val totalPage = productsApiService.requestProducts(page = page).execute().body()?.totalPages
        return (page + 1) == totalPage
    }

    override fun findByCategory(productId: Long): List<ProductDto> {
        val category = productsApiService.requestProduct(productId.toInt()).execute().body()?.category
        return productsApiService.requestProducts(category).execute().body()?.content
            ?: throw NoSuchElementException("there is no product with category: $category")
    }

    override fun shutDown(): Boolean {
        // TODO: 연결 끊기
        return false
    }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
