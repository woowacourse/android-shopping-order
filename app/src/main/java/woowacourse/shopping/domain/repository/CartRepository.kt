package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import java.lang.IllegalArgumentException
import kotlin.concurrent.Volatile

interface CartRepository {
    fun increaseQuantity(productId: Int)

    fun decreaseQuantity(productId: Int)

    fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    )

    fun deleteCartItem(productId: Int)

    fun find(productId: Int): CartItem

    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<CartItem>

    fun totalCartItemCount(): Int

    companion object {
        private const val NOT_INITIALIZE_INSTANCE_MESSAGE = "초기화된 인스턴스가 없습니다."

        @Volatile
        private var instance: CartRepository? = null

        fun setInstance(cartRepository: CartRepository) {
            synchronized(this) {
                instance = cartRepository
            }
        }

        fun getInstance(): CartRepository {
            return instance ?: throw IllegalArgumentException(NOT_INITIALIZE_INSTANCE_MESSAGE)
        }
    }
}
