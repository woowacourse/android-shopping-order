package woowacourse.shopping.presentation.view.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.Coupon

class CouponViewHolder(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
    }

    companion object {
        fun from(parent: ViewGroup): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding)
        }
    }
}
