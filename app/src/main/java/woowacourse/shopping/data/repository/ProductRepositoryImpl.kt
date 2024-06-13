package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.datasource.impl.RemoteCartDataSource
import woowacourse.shopping.data.datasource.impl.RemoteProductDataSource
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.domain.result.getOrNull
import woowacourse.shopping.domain.result.transForm
import kotlin.math.min

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource = RemoteProductDataSource(),
    private val cartDataSource: CartDataSource = RemoteCartDataSource(),
) :
    ProductRepository {
    override suspend fun getProductById(id: Long): Result<Product, DataError> =
        productDataSource.getProductsById(id).transForm { it.toProduct() }

    override suspend fun getAllProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>, DataError> = productDataSource.getProductsByOffset(page, size).transForm { it.toProductList() }

    override suspend fun getAllRecommendProducts(category: String): Result<List<Product>, DataError> {
        val carts: List<CartWithProduct> = cartWithProducts()

        var page = START_PRODUCT_PAGE
        var allProducts = mutableListOf<Product>()
        var loadedProducts = listOf<Product>()
        while (true) {
            val productResponse =
                productDataSource.getProductsByOffset(page, LOAD_PRODUCT_INTERVAL)
            when (productResponse) {
                is Result.Success ->
                    loadedProducts =
                        productResponse.data.toProductList()

                is Result.Error -> return Result.Error(productResponse.error)
            }

            if (noMoreProductOrMaxProduct(loadedProducts, allProducts)) break
            allProducts.addAll(loadedProducts.filterCategoryAndNotInCart(category, carts))
            page++
        }

        return Result.Success(
            allProducts.subList(0, min(MAX_RECOMMEND_SIZE, allProducts.size)),
        )
    }

    private suspend fun cartWithProducts(): List<CartWithProduct> {
        val count: Int =
            cartDataSource.getCartItemCounts().getOrNull()?.quantity
                ?: DEFAULT_CART_COUNT
        val cartResponse =
            cartDataSource.getCartItems(START_CART_PAGE, count).getOrNull()?.content
                ?: emptyList()
        return cartResponse.map { it.toCartWithProduct() }
    }

    private fun noMoreProductOrMaxProduct(
        loadedProducts: List<Product>,
        allProducts: MutableList<Product>,
    ) = loadedProducts.isEmpty() || allProducts.size >= MAX_RECOMMEND_SIZE

    private fun List<Product>.filterCategoryAndNotInCart(
        category: String,
        carts: List<CartWithProduct>,
    ) = this.filter { it.category == category }
        .filterNot { carts.map { it.product.id }.contains(it.id) }

    companion object {
        private const val DEFAULT_CART_COUNT = 300
        private const val LOAD_PRODUCT_INTERVAL = 30
        private const val START_CART_PAGE = 0
        private const val START_PRODUCT_PAGE = 0
        private const val MAX_RECOMMEND_SIZE = 10
    }
}
