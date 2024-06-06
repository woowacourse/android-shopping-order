package woowacourse.shopping.data.product

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override fun loadAllProducts(page: Int): List<Product> {
        return handleResponse(productDataSource.loadByPaged(page)).content.map { productDto ->
            productDto.toDomain(productQuantity(productDto.id))
        }
    }

    override fun loadProduct(id: Long): Product = handleResponse(productDataSource.loadById(id)).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean {
        val totalPages = handleResponse(productDataSource.loadByPaged(page)).totalPages
        return (page + 1) == totalPages
    }

    override fun shoppingCartProductQuantity(): Int = handleResponse(cartItemDataSource.fetchCartItems()).content.sumOf { it.quantity }

    private fun productQuantity(productId: Long): Int =
        handleResponse(cartItemDataSource.fetchCartItems()).content.find { it.product.id == productId }?.quantity ?: DEFAULT_QUANTITY

    private fun <T : Any> handleResponse(response: ResponseResult<T>): T {
        return when (response) {
            is ResponseResult.Success -> response.data
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    companion object {
        private const val TAG = "DefaultShoppingProductR"
        private const val DEFAULT_QUANTITY = 0
    }
}
