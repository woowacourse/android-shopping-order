package woowacourse.shopping.presentation.view.order.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.view.order.payment.CouponStateEventListener

class CouponAdapter(
    private val couponStateEventListener: CouponStateEventListener,
) : ListAdapter<DisplayModel<CouponUiModel>, CouponViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, couponStateEventListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val DiffCallBack =
            object : DiffUtil.ItemCallback<DisplayModel<CouponUiModel>>() {
                override fun areItemsTheSame(
                    oldItem: DisplayModel<CouponUiModel>,
                    newItem: DisplayModel<CouponUiModel>,
                ): Boolean = oldItem.data.id == newItem.data.id

                override fun areContentsTheSame(
                    oldItem: DisplayModel<CouponUiModel>,
                    newItem: DisplayModel<CouponUiModel>,
                ): Boolean = oldItem == newItem
            }
    }
}
