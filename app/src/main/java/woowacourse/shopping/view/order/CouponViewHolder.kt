package woowacourse.shopping.view.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    couponClickListener: OrderAdapter.CouponClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.couponClickListener = couponClickListener
    }

    fun bind(couponState: CouponState) {
        binding.couponState = couponState
    }

    companion object {
        fun of(
            parent: ViewGroup,
            couponClickListener: OrderAdapter.CouponClickListener,
        ): CouponViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
            return CouponViewHolder(binding, couponClickListener)
        }
    }
}
