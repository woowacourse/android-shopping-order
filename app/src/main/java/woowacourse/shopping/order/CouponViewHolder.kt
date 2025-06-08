package woowacourse.shopping.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.CouponItemBinding

class CouponViewHolder(
    val binding: CouponItemBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(coupon: Coupon) {
        binding.coupon = coupon
    }

    companion object {
        fun from(
            parent: ViewGroup,
            checkClickListener: CheckClickListener,
        ): CouponViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CouponItemBinding.inflate(inflater, parent, false)
            binding.checkClickListener = checkClickListener
            return CouponViewHolder(binding)
        }
    }
}
