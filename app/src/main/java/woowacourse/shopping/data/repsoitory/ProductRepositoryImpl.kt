package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingRemoteCartDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val shoppingRemoteCartDataSource: ShoppingRemoteCartDataSource,
) : ProductRepository {
    override suspend fun getCartById(productId: Long): Result<Cart> {
        val totalElements =
            shoppingRemoteCartDataSource.getCartProductsPaged(page = FIRST_PAGE, size = FIRST_SIZE).totalElements

        val cartsDto =
            shoppingRemoteCartDataSource.getCartProductsPaged(
                page = FIRST_PAGE,
                size = totalElements,
            ).content

        val product = productRemoteDataSource.findProductById(productId)

        return runCatching {
            cartsDto.firstOrNull { product.id == it.product.id } ?: Cart(
                product = product,
                quantity = INIT_QUANTITY,
            )
        }
    }

    override suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products> =
        runCatching {
            productRemoteDataSource.getPagingProduct(page, pageSize)
        }

    companion object {
        const val FIRST_PAGE = 0
        const val FIRST_SIZE = 1
        const val INIT_QUANTITY = 1
    }
}