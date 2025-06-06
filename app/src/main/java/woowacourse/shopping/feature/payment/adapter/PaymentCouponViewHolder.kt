package woowacourse.shopping.feature.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon

class PaymentCouponViewHolder(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
    }

    companion object {
        fun from(parent: ViewGroup): PaymentCouponViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
            return PaymentCouponViewHolder(binding)
        }
    }
}
