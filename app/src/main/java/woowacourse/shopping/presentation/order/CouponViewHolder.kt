package woowacourse.shopping.presentation.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCouponBinding

class CouponViewHolder(
    parent: ViewGroup,
    private val couponClickListener: CouponClickListener,
) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false),
    ) {
    private val binding: ItemCouponBinding = ItemCouponBinding.bind(itemView)

    init {
        binding.clickListener = couponClickListener
    }

    fun bind(coupon: CouponUiModel) {
        binding.coupon = coupon
    }
}
