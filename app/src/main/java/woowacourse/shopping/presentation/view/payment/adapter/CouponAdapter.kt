package woowacourse.shopping.presentation.view.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.common.model.CouponUiModel

class CouponAdapter(
    private val eventListener: CouponEventListener,
) : ListAdapter<CouponUiModel, CouponViewHolder>(CouponDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, eventListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    interface CouponEventListener {
        fun onSelectCoupon(couponId: Long)
    }
}
