package woowacourse.shopping.data.cart

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.domain.repository.cart.CartItemRepository
import woowacourse.shopping.remote.cart.CartItemDto.Companion.toDomain
import woowacourse.shopping.ui.model.CartItem

class DefaultCartItemRepository(
    private val cartItemDataSource: CartItemDataSource,
) : CartItemRepository {

    override fun loadCartItems(): List<CartItem> {
        return handleResponse(cartItemDataSource.loadAllCartItems()).content.map { cartItemDto -> cartItemDto.toDomain() }
    }

    override fun addCartItem(
        id: Long,
        quantity: Int,
    ) {
        val cartItem = loadCartItems().find { it.product.id == id }
        if (cartItem == null) {
            handleResponse(cartItemDataSource.addedNewProductsId(ProductIdsCount(id, quantity)))
            return
        }
        handleResponse(cartItemDataSource.plusProductsIdCount(cartItem.id, quantity))
    }

    override fun removeCartItem(id: Long) {
        cartItemDataSource.removedProductsId(id)
    }

    override fun increaseCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val cartItem = loadCartItems().find { it.product.id == id } ?: throw NoSuchElementException()
        handleResponse(cartItemDataSource.plusProductsIdCount(cartItem.id, quantity))
    }

    override fun decreaseCartProduct(
        id: Long,
        quantity: Int,
    ) {
        val cartItem = loadCartItems().find { it.product.id == id } ?: throw NoSuchElementException()
        handleResponse(cartItemDataSource.minusProductsIdCount(cartItem.id, quantity))
    }

    override fun increaseCartItem(
        cartItemId: Long,
        quantity: Int,
    ) {
        handleResponse(cartItemDataSource.plusProductsIdCount(cartItemId, quantity))
    }

    private fun <T : Any> handleResponse(response: ResponseResult<T>): T {
        return when(response) {
            is ResponseResult.Success -> response.data
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }
}
