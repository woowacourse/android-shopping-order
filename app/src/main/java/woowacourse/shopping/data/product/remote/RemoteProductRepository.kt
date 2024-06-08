package woowacourse.shopping.data.product.remote

import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

object RemoteProductRepository : ProductRepository {
    private const val MAX_PRODUCT_COUNT = 9999999
    private const val RECOMMEND_PRODUCTS_COUNT = 10

    override suspend fun find(id: Int): Result<Product> {
        return retrofitApi.requestProduct(id = id).map { it.toProduct() }
    }

    override suspend fun findPage(
        page: Int,
        pageSize: Int,
    ): Result<List<Product>> {
        return retrofitApi.requestProducts(page = page, size = pageSize).map { it.toProductList() }
    }

    override suspend fun isLastPage(
        page: Int,
        pageSize: Int,
    ): Result<Boolean> {
        return retrofitApi.requestProducts(page = page, size = pageSize).map { it.last }
    }

    override suspend fun findRecommendProducts(
        category: String,
        cartItems: List<CartItem>,
    ): Result<List<Product>> {
        return findCategoryProducts(category)
            .map { categoryProducts ->
                categoryProducts
                    .filter { product -> cartItems.none { it.product.id == product.id } }
                    .take(RECOMMEND_PRODUCTS_COUNT)
            }
    }

    private suspend fun findCategoryProducts(category: String): Result<List<Product>> {
        return retrofitApi.requestProducts(category = category, page = 0, size = MAX_PRODUCT_COUNT)
            .map { it.toProductList() }
    }
}
