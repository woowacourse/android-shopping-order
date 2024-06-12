package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource,
    private val shoppingCartDataSource: ShoppingCartDataSource,
) : ProductRepository {
    override suspend fun findCartByProductId(id: Long): Result<Cart> {
        val totalElements = shoppingCartDataSource.getCartProductTotalElements().getOrThrow()

        val cartsDto =
            shoppingCartDataSource.getCartProductsPaged(page = FIRST_PAGE, size = totalElements)
                .map { cartDto -> cartDto.content.map { it } }
                .getOrNull()

        return productDataSource.findProductById(id).mapCatching { productDto ->
            val cartDto = cartsDto?.firstOrNull { it.product.id == productDto.id }
            cartDto?.toDomain() ?: Cart(
                product = productDto.toDomain(),
                quantity = INIT_QUANTITY,
            )
        }
    }

    override suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products> =
        productDataSource.getPagingProduct(page, pageSize)
            .mapCatching { result -> result.toDomain() }

    companion object {
        const val FIRST_PAGE = 0
        const val FIRST_SIZE = 1
        const val INIT_QUANTITY = 1
    }
}
