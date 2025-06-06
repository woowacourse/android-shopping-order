package woowacourse.shopping.feature.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon

class PaymentCouponViewHolder(
    private val binding: ItemCouponBinding,
    private val couponClickListener: CouponClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
        binding.couponClickListener = couponClickListener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            listener: CouponClickListener,
        ): PaymentCouponViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
            return PaymentCouponViewHolder(binding, listener)
        }
    }

    interface CouponClickListener {
        fun onCouponCheck(coupon: Coupon)
    }
}
