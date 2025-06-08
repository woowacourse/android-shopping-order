package woowacourse.shopping.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.Coupon

class CouponViewHolder(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        coupon: Coupon,
        isSelected: Boolean,
    ) {
        binding.coupon = coupon
        binding.isSelected = isSelected
    }

    companion object {
        fun from(parent: ViewGroup): CouponViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(inflater, parent, false)
            return CouponViewHolder(binding)
        }
    }
}
