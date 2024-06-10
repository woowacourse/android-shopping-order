package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.datasource.impl.RemoteCartDataSource
import woowacourse.shopping.data.datasource.impl.RemoteProductDataSource
import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.api.result
import woowacourse.shopping.data.remote.api.resultOrNull
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.domain.result.handleApiResult
import woowacourse.shopping.domain.result.handleError
import woowacourse.shopping.domain.result.result
import woowacourse.shopping.domain.result.resultOrNull
import kotlin.math.min

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource = RemoteProductDataSource(),
    private val cartDataSource: CartDataSource = RemoteCartDataSource(),
) :
    ProductRepository {
    override suspend fun productById(id: Long): Product =
        handleApiResult(
            productDataSource.getProductsById(id),
            ResponseProductIdGetDto::toProduct,
        ).result()

    override suspend fun productByIdOrNull(id: Long): Product? =
        handleApiResult(
            productDataSource.getProductsById(id),
            ResponseProductIdGetDto::toProduct,
        ).resultOrNull()

    override suspend fun productByIdResponse(id: Long): Result<Product> =
        handleApiResult(
            productDataSource.getProductsById(id),
            ResponseProductIdGetDto::toProduct,
        )

    override suspend fun allProducts(
        page: Int,
        size: Int,
    ): List<Product> = handleApiResult(
                productDataSource.getProductsByOffset(page, size),
                transform = ResponseProductsGetDto::toProductList,
            ).result()



    override suspend fun allProductsResponse(
        page: Int,
        size: Int,
    ): Result<List<Product>> =
        handleApiResult(
            productDataSource.getProductsByOffset(page, size),
            transform = ResponseProductsGetDto::toProductList,
        )

    override suspend fun allRecommendProducts(category: String): List<Product> {
        val carts: List<CartWithProduct> = cartWithProducts()
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
        return allProducts
    }

    override suspend fun allRecommendProductsResponse(category: String): Result<List<Product>> {
        try {
            val carts: List<CartWithProduct> = cartWithProducts()

            var page = START_PRODUCT_PAGE
            var allProducts = mutableListOf<Product>()
            var loadedProducts = listOf<Product>()
            while (true) {
                val productResponse =
                    productDataSource.getProductsByOffset(page, LOAD_PRODUCT_INTERVAL)
                when (productResponse) {
                    is ApiResponse.Success ->
                        loadedProducts =
                            productResponse.data.toProductList()

                    is ApiResponse.Error -> return handleError(productResponse)
                    is ApiResponse.Exception -> return Result.Exception(
                        productResponse.e,
                    )
                }

                if (noMoreProductOrMaxProduct(loadedProducts, allProducts)) break
                allProducts.addAll(loadedProducts.filterCategoryAndNotInCart(category, carts))
                page++
            }

            return Result.Success(
                allProducts.subList(0, min(MAX_RECOMMEND_SIZE, allProducts.size)),
            )
        } catch (e: Exception) {
            return Result.Exception(e)
        }

    }

    private suspend fun cartWithProducts(): List<CartWithProduct> {
        val count: Int =
            cartDataSource.getCartItemCounts().resultOrNull()?.quantity
                ?: DEFAULT_CART_COUNT
        val cartResponse =
            cartDataSource.getCartItems(START_CART_PAGE, count).resultOrNull()?.content
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
