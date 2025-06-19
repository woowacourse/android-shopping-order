package woowacourse.shopping.feature.order

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class CouponAdapter(
    private val couponClickListener: CouponClickListener,
) : ListAdapter<CouponState, CouponViewHolder>(CouponDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, couponClickListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
