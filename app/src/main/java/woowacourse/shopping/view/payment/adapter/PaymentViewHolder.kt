package woowacourse.shopping.view.payment.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.Coupon

class PaymentViewHolder(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
        Log.d("hwannow_log", "bind")
    }

    companion object {
        fun from(parent: ViewGroup): PaymentViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(inflater, parent, false)
            return PaymentViewHolder(binding)
        }
    }
}
