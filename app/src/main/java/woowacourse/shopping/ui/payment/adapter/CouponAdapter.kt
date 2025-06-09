package woowacourse.shopping.ui.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.payment.adapter.CouponViewHolder.OnclickHandler

class CouponAdapter(
    private val onclickHandler: OnclickHandler,
) : ListAdapter<CouponUiModel, CouponViewHolder>(
        object : DiffUtil.ItemCallback<CouponUiModel>() {
            override fun areItemsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CouponUiModel,
                newItem: CouponUiModel,
            ): Boolean = oldItem == newItem
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, onclickHandler)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
