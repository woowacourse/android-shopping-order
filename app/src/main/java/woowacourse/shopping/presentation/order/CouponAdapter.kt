package woowacourse.shopping.presentation.order

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CouponAdapter(
    private val clickListener: CouponClickListener
) : ListAdapter<CouponUiModel, CouponViewHolder>(DiffCallback) {
    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder(parent, clickListener)

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CouponUiModel>() {
            override fun areContentsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}