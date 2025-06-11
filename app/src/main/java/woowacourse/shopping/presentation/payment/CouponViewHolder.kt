package woowacourse.shopping.presentation.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.CouponItemBinding
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.presentation.payment.event.CouponEventHandler

class CouponViewHolder(
    parent: ViewGroup,
    private val handler: CouponEventHandler,
) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.coupon_item, parent, false),
    ) {
    private val binding = CouponItemBinding.bind(itemView)

    fun bind(
        coupon: Coupon,
        isChecked: Boolean,
    ) {
        binding.coupon = coupon
        binding.isChecked = isChecked
        binding.handler = handler
    }
}
