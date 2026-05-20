package woowacourse.shopping.ui.util

import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.Products
import woowacourse.shopping.ui.cart.CartItemUiModel
import woowacourse.shopping.ui.productList.ProductUiModel

fun List<CartItem>.toUiModel(
    selectedItems: Set<Int>,
): List<CartItemUiModel> =
    this.map { cartItem ->
        val isSelected = selectedItems.contains(cartItem.id)
        cartItem.toUiModel(isSelected)
    }

fun CartItem.toUiModel(isSelected: Boolean): CartItemUiModel =
    CartItemUiModel(
        id = id,
        product = product,
        quantity = quantity.value,
        isSelected = isSelected,
        totalPrice = totalPrice,
    )


fun Products.toUiModel(cartItems: CartItems): List<ProductUiModel> =
    this.items.toUiModel(cartItems)

fun List<Product>.toUiModel(cartItems: CartItems): List<ProductUiModel> =
    this.map { product ->
        product.toUiModel(cartItems.findQuantity(product.id).value)
    }

fun Product.toUiModel(cartAmount: Int): ProductUiModel =
    ProductUiModel(
        id = this.id,
        name = this.name.value,
        price = formatPrice(this.price.value),
        imageUrl = this.imageUrl.value,
        cartAmount = cartAmount.toString(),
        showAmountController = cartAmount > 0,
    )
