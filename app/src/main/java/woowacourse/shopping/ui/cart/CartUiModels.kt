package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

class CartUiModels(val uiModels: List<CartUiModel> = emptyList()) {
    val size: Int get() = uiModels.size

    fun isEmpty() = uiModels.isEmpty()

    fun toCartItems(): List<CartItem> {
        return uiModels.map { CartItem(it.cartItemId, it.productId, it.quantity) }
    }

    fun selectedTotalCount(): Int {
        return uiModels.count { it.isSelected }
    }

    fun selectedTotalQuantity(): Int {
        return uiModels
            .filter { it.isSelected }
            .sumOf { it.quantity.count }
    }

    fun selectedTotalPrice(): Int {
        return uiModels
            .filter { it.isSelected }
            .sumOf { it.totalPrice() }
    }

    fun selectedCartItemIds(): List<Int> {
        return uiModels
            .filter { it.isSelected }
            .map { it.cartItemId }
    }

    fun findByProductId(productId: Int): CartUiModel? {
        return uiModels.find { it.productId == productId }
    }

    fun upsert(newCartUiModel: CartUiModel): CartUiModels {
        val newCartUiModels = uiModels.toMutableList()
        if (uiModels.none { it.cartItemId == newCartUiModel.cartItemId }) {
            newCartUiModels += newCartUiModel
            return CartUiModels(newCartUiModels)
        }

        val position = uiModels.indexOfFirst { it.cartItemId == newCartUiModel.cartItemId }
        newCartUiModels[position] = newCartUiModel
        return CartUiModels(newCartUiModels)
    }

    fun remove(deletedCartUiModel: CartUiModel): CartUiModels {
        val newCartUiModels = uiModels.toMutableList()
        newCartUiModels.remove(deletedCartUiModel)
        return CartUiModels(newCartUiModels)
    }

    fun select(
        productId: Int,
        isSelected: Boolean,
    ): CartUiModels {
        val oldCartUiModel = findByProductId(productId) ?: return this
        if (oldCartUiModel.isSelected == isSelected) return this

        val newCartUiModel = oldCartUiModel.copy(isSelected = isSelected)
        return upsert(newCartUiModel)
    }

    fun newCartUiModelQuantity(
        product: Product,
        cartItem: CartItem,
    ): CartUiModel {
        val oldCartUiModel = findByProductId(product.id) ?: CartUiModel.from(product, cartItem)
        return oldCartUiModel.copy(quantity = cartItem.quantity)
    }
}
