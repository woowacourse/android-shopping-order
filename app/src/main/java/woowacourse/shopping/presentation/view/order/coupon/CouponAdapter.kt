package woowacourse.shopping.presentation.view.order.coupon

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CouponUiModel

class CouponAdapter(
    private val selectCouponListener: SelectCouponListener,
) : ListAdapter<CouponUiModel, CouponViewHolder>(couponDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder = CouponViewHolder.from(parent, selectCouponListener)

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val couponDiffCallback =
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

    interface SelectCouponListener {
        fun onSelectCoupon(coupon: CouponUiModel)
    }
}
