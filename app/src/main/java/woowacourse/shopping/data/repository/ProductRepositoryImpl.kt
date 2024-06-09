package woowacourse.shopping.data.repository

import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.ApiHandleCartDataSource
import woowacourse.shopping.data.datasource.ApiHandleProductDataSource
import woowacourse.shopping.data.datasource.impl.ApiHandleCartDataSourceImpl
import woowacourse.shopping.data.datasource.impl.ApiHandleProductDataSourceImpl
import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.api.result
import woowacourse.shopping.data.remote.api.resultOrNull
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Response
import woowacourse.shopping.domain.result.handleApiResult
import woowacourse.shopping.domain.result.handleError
import woowacourse.shopping.domain.result.result
import woowacourse.shopping.domain.result.resultOrNull
import kotlin.math.min

class ProductRepositoryImpl(
    private val productDataSource: ApiHandleProductDataSource = ApiHandleProductDataSourceImpl(),
    private val cartDataSource: ApiHandleCartDataSource = ApiHandleCartDataSourceImpl(),
) :
    ProductRepository {

    override suspend fun productById(id: Long): Product = handleApiResult(
        productDataSource.getProductsById(id), ResponseProductIdGetDto::toProduct
    ).result()

    override suspend fun productByIdOrNull(id: Long): Product? = handleApiResult(
        productDataSource.getProductsById(id), ResponseProductIdGetDto::toProduct
    ).resultOrNull()

    override suspend fun productByIdResponse(id: Long): Response<Product> = handleApiResult(
        productDataSource.getProductsById(id), ResponseProductIdGetDto::toProduct
    )

    override suspend fun allProducts(page: Int, size: Int): List<Product> {
        val result = handleApiResult(
            productDataSource.getProductsByOffset(page, size),
            transform = ResponseProductsGetDto::toProductList
        )
        return if (result is Fail.NotFound) emptyList() else result.result()
    }

    override suspend fun allProductsResponse(page: Int, size: Int): Response<List<Product>> =
        handleApiResult(
            productDataSource.getProductsByOffset(page, size),
            transform = ResponseProductsGetDto::toProductList
        )

    override suspend fun allProductsByCategory(category: String): List<Product> = coroutineScope {
        val count: Int = cartDataSource.getCartItemCounts().resultOrNull()?.quantity
            ?: DEFAULT_CART_COUNT
        val cartResponse =
            cartDataSource.getCartItems(START_CART_PAGE, count).resultOrNull()?.content
                ?: emptyList()
        val carts: List<CartWithProduct> = cartResponse.map { it.toCartWithProduct() }
        var page = START_PRODUCT_PAGE
        var allProducts = mutableListOf<Product>()
        var loadedProducts = emptyList<Product>()
        while (true) {
            loadedProducts =
                productDataSource.getProductsByOffset(page, LOAD_PRODUCT_INTERVAL).result()
                    .toProductList()

            if (noMoreProductOrMaxProduct(loadedProducts, allProducts)) break
            allProducts.addAll(loadedProducts.filterCategoryAndNotInCart(category, carts))
            page++
        }
        return@coroutineScope allProducts
    }

    override suspend fun allProductsByCategoryResponse(category: String): Response<List<Product>> =
        coroutineScope {
            try {
                val count: Int = cartDataSource.getCartItemCounts().resultOrNull()?.quantity
                    ?: DEFAULT_CART_COUNT
                val cartResponse =
                    cartDataSource.getCartItems(START_CART_PAGE, count).resultOrNull()?.content
                        ?: emptyList()
                val carts: List<CartWithProduct> = cartResponse.map { it.toCartWithProduct() }

                var page = START_PRODUCT_PAGE
                var allProducts = mutableListOf<Product>()
                var loadedProducts = listOf<Product>()
                while (true) {
                    val productResponse =
                        productDataSource.getProductsByOffset(page, LOAD_PRODUCT_INTERVAL)
                    when (productResponse) {
                        is ApiResponse.Success -> loadedProducts =
                            productResponse.data.toProductList()

                        is ApiResponse.Error -> return@coroutineScope handleError(productResponse)
                        is ApiResponse.Exception -> return@coroutineScope Response.Exception(
                            productResponse.e,
                        )
                    }

                    if (noMoreProductOrMaxProduct(loadedProducts, allProducts)) break
                    allProducts.addAll(loadedProducts.filterCategoryAndNotInCart(category, carts))
                    page++
                }

                return@coroutineScope Response.Success(
                    allProducts.subList(0, min(MAX_RECOMMEND_SIZE, allProducts.size)),
                )
            } catch (e: Exception) {
                return@coroutineScope Response.Exception(e)
            }
        }

    private fun noMoreProductOrMaxProduct(
        loadedProducts: List<Product>,
        allProducts: MutableList<Product>
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
