package woowacourse.shopping.presentation.ui.payment.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.CartProduct

@Parcelize
data class PaymentUiModel(
    val couponUiModels: List<CouponUiModel> = emptyList(),
    val cartProducts: List<CartProduct> = emptyList()
): Parcelable {
    val cartProductIds get() = cartProducts.map { it.cartId }
    val totalPrice get() = cartProducts.sumOf { it.price * it.quantity }
}