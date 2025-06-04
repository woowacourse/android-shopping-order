package woowacourse.shopping.presentation.order

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CouponUiModel

class CouponAdapter(
    private val itemClickListener: CouponClickListener,
) : ListAdapter<CouponUiModel, CouponViewHolder>(CouponDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.create(parent, itemClickListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }
}
