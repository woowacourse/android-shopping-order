package woowacourse.shopping.presentation.cart

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.presentation.shopping.toDomain
import woowacourse.shopping.presentation.shopping.toUiModel

fun Cart.toUiModel(): List<CartProductUi> {
    return cartProducts.map { it.toUiModel() }
}

fun CartProduct.toUiModel(): CartProductUi {
    return CartProductUi(id, product.toUiModel(), count)
}

fun CartProductUi.toDomain(): CartProduct {
    return CartProduct(id = id, product = product.toDomain(), count = count)
}

fun List<CartProductUi>.toCart(): Cart {
    return Cart(map { it.toDomain() })
}

fun CartErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        CartErrorEvent.LoadCartProducts -> context.getString(R.string.error_msg_load_cart_products)
        CartErrorEvent.CanLoadMoreCartProducts -> context.getString(R.string.error_msg_load_cart_products)
        CartErrorEvent.UpdateCartProducts -> context.getString(R.string.error_msg_update_cart_products)
        CartErrorEvent.DecreaseCartCountLimit -> context.getString(R.string.error_msg_decrease_cart_count_limit)
        CartErrorEvent.DeleteCartProduct -> context.getString(R.string.error_msg_delete_cart_product)
        CartErrorEvent.EmptyOrderProduct -> context.getString(R.string.error_msg_empty_order_product)
    }
}
