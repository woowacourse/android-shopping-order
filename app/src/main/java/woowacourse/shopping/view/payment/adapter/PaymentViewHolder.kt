package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon

class PaymentViewHolder(
    private val binding: ItemCouponBinding,
    couponSelectListener: CouponSelectListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.couponSelectListener = couponSelectListener
    }

    fun bind(couponItem: CouponItem) {
        binding.coupon = couponItem.coupon
        binding.isSelected = couponItem.isSelected
        when (couponItem.coupon) {
            is FixedCoupon -> binding.minimumAmount = couponItem.coupon.minimumAmount
            is FreeShippingCoupon -> binding.minimumAmount = couponItem.coupon.minimumAmount
            else -> binding.minimumAmount = null
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            couponSelectListener: CouponSelectListener,
        ): PaymentViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(inflater, parent, false)
            return PaymentViewHolder(binding, couponSelectListener)
        }
    }
}
