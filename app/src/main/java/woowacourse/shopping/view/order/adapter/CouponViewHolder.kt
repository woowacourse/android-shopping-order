package woowacourse.shopping.view.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.view.core.base.BaseViewHolder

class CouponViewHolder(
    parent: ViewGroup,
    private val handler: Handler,
) : BaseViewHolder<ItemCouponBinding>(
        ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    fun bind(item: OrderRvItems.CouponItem) {
        with(binding) {
            model = item.coupon
            eventHandler = handler
        }
    }

    interface Handler {
        fun onChangeCheckedState(couponId: Int)
    }
}
