package woowacourse.shopping.view.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    couponListener: CouponListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.couponListener = couponListener
    }

    fun bind(coupon: CouponItem) {
        binding.coupon = coupon
    }

    companion object {
        fun from(
            parent: ViewGroup,
            couponListener: CouponListener,
        ): CouponViewHolder {
            val binding =
                ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CouponViewHolder(binding, couponListener)
        }
    }

    fun interface CouponListener {
        fun onCouponClick(couponId: Int)
    }
}
