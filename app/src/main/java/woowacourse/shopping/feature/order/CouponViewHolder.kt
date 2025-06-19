package woowacourse.shopping.feature.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val couponClickListener: CouponClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: CouponState) {
        binding.coupon = coupon
        binding.listener = couponClickListener
    }

    companion object {
        fun from(
            parent: ViewGroup,
            couponClickListener: CouponClickListener,
        ): CouponViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
            return CouponViewHolder(binding, couponClickListener)
        }
    }
}
