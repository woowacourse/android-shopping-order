package woowacourse.shopping.ui.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.Coupon

class PaymentCouponAdapter(
    private val onClickHandler: PaymentCouponViewHolder.OnClickHandler,
) : ListAdapter<Coupon, PaymentCouponViewHolder>(PaymentCouponDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PaymentCouponViewHolder = PaymentCouponViewHolder.from(parent, onClickHandler)

    override fun onBindViewHolder(
        holder: PaymentCouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
