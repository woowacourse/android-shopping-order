package woowacourse.shopping.presentation.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.databinding.CouponItemBinding

class CouponViewHolder(
    parent: ViewGroup,
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.coupon_item, parent, false)
) {
    private val binding = CouponItemBinding.bind(itemView)

    fun bind(coupon: Coupon) {
        binding.coupon = coupon
    }
}