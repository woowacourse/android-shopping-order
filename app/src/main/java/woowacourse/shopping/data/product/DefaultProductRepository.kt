package woowacourse.shopping.data.product

import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.common.ResponseHandlingUtils.handleResponse
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override fun loadProducts(page: Int): List<Product> {
        return handleResponse(productDataSource.loadByPaged(page)).content.map { productDto ->
            productDto.toDomain(findCartItemQuantity(productDto.id))
        }
    }

    override fun loadProduct(id: Long): Product = handleResponse(productDataSource.loadById(id))
        .toDomain(findCartItemQuantity(id))

    override fun isFinalPage(page: Int): Boolean {
        val totalPages = handleResponse(productDataSource.loadByPaged(page)).totalPages
        return (page + 1) == totalPages
    }

    private fun findCartItemQuantity(productId: Long): Int =
        handleResponse(cartItemDataSource.fetchCartItems()).content.find { it.product.id == productId }?.quantity ?: DEFAULT_QUANTITY

    companion object {
        private const val TAG = "DefaultShoppingProductR"
        private const val DEFAULT_QUANTITY = 0
    }
}
