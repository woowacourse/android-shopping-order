package woowacourse.shopping.view.order

import androidx.annotation.StringRes
import woowacourse.shopping.domain.order.Coupon
import woowacourse.shopping.domain.order.DiscountType

data class CouponItem(
    val id: Int,
    val description: String,
    val code: String,
    @StringRes val conditionResId: Int? = null,
    val conditionArgs: List<Any> = emptyList(),
    val expirationDate: String,
    val type: DiscountType,
    val isSelected: Boolean = false,
    val origin: Coupon,
)
