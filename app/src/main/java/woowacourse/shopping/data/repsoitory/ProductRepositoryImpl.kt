package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingRemoteCartDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val shoppingRemoteCartDataSource: ShoppingRemoteCartDataSource,
) : ProductRepository {
    override fun getCartById(productId: Long): Result<Cart> {
        val totalElements =
            shoppingRemoteCartDataSource.getCartProductsPaged(page = FIRST_PAGE, size = FIRST_SIZE)
                .getOrThrow().totalElements

        val cartsDto =
            shoppingRemoteCartDataSource.getCartProductsPaged(page = FIRST_PAGE, size = totalElements)
                .map { cartDto -> cartDto.content.map { it } }
                .getOrNull()

        return productRemoteDataSource.findProductById(productId).mapCatching { productDto ->
            val cartProduct = cartsDto?.firstOrNull { it.productDto.id == productDto.id }
            cartProduct?.toDomain() ?: Cart(
                product = productDto.toDomain(),
                quantity = INIT_QUANTITY,
            )
        }
    }

    override fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products> =
        productRemoteDataSource.getPagingProduct(page, pageSize)
            .mapCatching { result -> result.toDomain() }

    companion object {
        const val FIRST_PAGE = 0
        const val FIRST_SIZE = 1
        const val INIT_QUANTITY = 1
    }
}
