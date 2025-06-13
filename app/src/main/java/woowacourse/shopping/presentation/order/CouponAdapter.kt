package woowacourse.shopping.presentation.order

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.uimodel.CouponUiModel

class CouponAdapter(
    private val couponClickListener: CouponClickListener,
) : ListAdapter<CouponUiModel, CouponViewHolder>(couponDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.create(parent, couponClickListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val couponDiffCallback =
            object : DiffUtil.ItemCallback<CouponUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}
