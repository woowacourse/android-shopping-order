package woowacourse.shopping.view.payment

sealed interface CouponsItem {
    val viewType: CouponsItemViewType

    data object Header : CouponsItem {
        override val viewType: CouponsItemViewType = CouponsItemViewType.HEADER
    }

    data class Coupon(
        val value: woowacourse.shopping.domain.payment.Coupon,
        val selected: Boolean,
    ) : CouponsItem {
        override val viewType: CouponsItemViewType = CouponsItemViewType.COUPON
    }
}
