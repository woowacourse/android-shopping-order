package woowacourse.shopping.presentation.common

import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.UpdateItemResult
import woowacourse.shopping.domain.repository.CartRepository

suspend fun addToCartUseCase(
    cartRepository: CartRepository,
    productId: Long,
    quantity: Int = 1,
): AddItemResult =
    try {
        val item = cartRepository.getCart().items.find { it.product.id == productId }

        if (item != null) {
            val updatedResult = cartRepository.changeCartItem(productId, item.increase(quantity).quantity)
            when (updatedResult) {
                is UpdateItemResult.Success -> AddItemResult.Incremented(updatedResult.cart)
                is UpdateItemResult.Error -> AddItemResult.Error(updatedResult.message)
            }
        } else {
            cartRepository.addItem(productId, quantity)
        }
    } catch (e: Exception) {
        AddItemResult.Error("장바구니 담기에 실패했습니다.")
    }
