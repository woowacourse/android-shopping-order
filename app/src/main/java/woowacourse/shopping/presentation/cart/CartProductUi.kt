package woowacourse.shopping.presentation.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.presentation.shopping.detail.ProductUi
import woowacourse.shopping.presentation.shopping.toDomain
import woowacourse.shopping.presentation.shopping.toUiModel

@Parcelize
data class CartProductUi(
    val id: Long = 0,
    val product: ProductUi,
    val count: Int = 0,
    val isSelected: Boolean = true,
) : Parcelable {
    val isVisible: Boolean
        get() = count > 0
    val totalPrice: Int
        get() = product.price * count
}

fun Cart.toUiModel(): List<CartProductUi> {
    return cartProducts.map { it.toUiModel() }
}

fun CartProduct.toUiModel(): CartProductUi {
    return CartProductUi(id, product.toUiModel(), count)
}

fun CartProductUi.toDomain(): CartProduct {
    return CartProduct(id = id, product = product.toDomain(), count = count)
}