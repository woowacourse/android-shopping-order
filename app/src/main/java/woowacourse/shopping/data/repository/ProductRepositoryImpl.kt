package woowacourse.shopping.data.repository

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.ApiHandleCartDataSource
import woowacourse.shopping.data.datasource.ApiHandleProductDataSource
import woowacourse.shopping.data.datasource.impl.ApiHandleCartDataSourceImpl
import woowacourse.shopping.data.datasource.impl.ApiHandleProductDataSourceImpl
import woowacourse.shopping.data.local.room.cart.Cart
import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.math.min

class ProductRepositoryImpl(
    private val productDataSource: ApiHandleProductDataSource = ApiHandleProductDataSourceImpl(),
    private val cartDataSource: ApiHandleCartDataSource = ApiHandleCartDataSourceImpl()
) :
    ProductRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ApiResponse<List<Product>> = handleResponse(
        productDataSource.getProductsByOffset(page, size),
        transform = ::toProductList
    )


    override suspend fun find(id: Long): ApiResponse<Product> =
        handleResponse(productDataSource.getProductsById(id), transform = ::toProduct)

    override suspend fun productsByCategory(category: String): ApiResponse<List<Product>> =
        coroutineScope {

            try {
                val countResponse = async(exceptionHandler()) { cartDataSource.getCartItemCounts() }.await()
                val count: Int = when (countResponse) {
                    is ApiResult.Success -> countResponse.data.quantity
                    is ApiResult.Error -> DEFAULT_CART_COUNT
                    is ApiResult.Exception -> DEFAULT_CART_COUNT
                }

                val cartResponse = async(exceptionHandler()) { cartDataSource.getCartItems(START_CART_PAGE, count) }.await()

                val carts: List<Cart> = when (cartResponse) {
                    is ApiResult.Success -> cartResponse.data.content.map { it.toCart() }
                    is ApiResult.Error -> return@coroutineScope handleError(cartResponse)
                    is ApiResult.Exception -> return@coroutineScope ApiResponse.Exception(cartResponse.e)
                }

                var page = START_PRODUCT_PAGE
                var products = mutableListOf<Product>()
                var loadedProducts = listOf<Product>()
                while (true) {
                    val productResponse = async(exceptionHandler()) { productDataSource.getProductsByOffset(page, LOAD_PRODUCT_INTERVAL) }.await()
                    when (productResponse) {
                        is ApiResult.Success -> loadedProducts = toProductList(productResponse.data)
                        is ApiResult.Error -> return@coroutineScope handleError(productResponse)
                        is ApiResult.Exception -> return@coroutineScope ApiResponse.Exception(
                            productResponse.e
                        )
                    }

                    if (loadedProducts.isEmpty() || products.size >= MAX_RECOMMEND_SIZE) break
                    products.addAll(loadedProducts.filterCategoryAndNotInCart(category, carts))
                    page++
                }

                return@coroutineScope ApiResponse.Success(products.subList(0, min(MAX_RECOMMEND_SIZE, products.size)))
            } catch (e: Exception) {
                return@coroutineScope ApiResponse.Exception(e)
            }
        }

    private fun List<Product>.filterCategoryAndNotInCart(category: String, carts: List<Cart>) =
        this.filter { it.category == category }
            .filterNot { carts.map { it.productId }.contains(it.id) }

    private fun exceptionHandler() = CoroutineExceptionHandler { _, exception -> throw exception }

    companion object {
        private const val DEFAULT_CART_COUNT = 300
        private const val LOAD_PRODUCT_INTERVAL = 30
        private const val START_CART_PAGE = 0
        private const val START_PRODUCT_PAGE = 0
        private const val MAX_RECOMMEND_SIZE = 10
    }


}

