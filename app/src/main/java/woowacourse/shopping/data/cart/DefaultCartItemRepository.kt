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
        return when(val response = cartItemDataSource.loadAllCartItems()) {
            is ResponseResult.Success -> response.data.content.map { cartItemDto -> cartItemDto.toDomain() }
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}:예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun addCartItem(
        id: Long,
        quantity: Int,
    ) {
        val cartItem = loadCartItems().find { it.product.id == id }
            ?: when(val response = cartItemDataSource.addedNewProductsId(ProductIdsCount(id, quantity))) {
                is ResponseResult.Success -> return
                is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
                is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
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
        handleResponse(cartItemDataSource.minusProductsIdCount(id, quantity))
    }

    override fun increaseCartItem(
        cartItemId: Long,
        quantity: Int,
    ) {
        handleResponse(cartItemDataSource.plusProductsIdCount(cartItemId, quantity))
    }

    private fun <T : Any> handleResponse(response: ResponseResult<T>) {
        when(response) {
            is ResponseResult.Success -> return
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }
}
