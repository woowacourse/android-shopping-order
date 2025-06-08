package woowacourse.shopping.presentation.view.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.view.checkout.CheckoutEventHandler

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    checkoutEventHandler: CheckoutEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.eventHandler = checkoutEventHandler
    }

    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
    }

    companion object {
        fun from(
            parent: ViewGroup,
            checkoutEventHandler: CheckoutEventHandler,
        ): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding, checkoutEventHandler)
        }
    }
}
