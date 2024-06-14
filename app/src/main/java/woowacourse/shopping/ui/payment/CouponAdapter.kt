package woowacourse.shopping.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.HolderCouponBinding
import woowacourse.shopping.ui.model.CouponUi

class CouponAdapter(
    private val couponCheckListener: CouponCheckListener,
) : ListAdapter<CouponUi, CouponViewHolder>(couponComparator) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder =
        CouponViewHolder(
            HolderCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            couponCheckListener,
        )

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        const val TAG = "CouponAdapter"

        private val couponComparator =
            object : DiffUtil.ItemCallback<CouponUi>() {
                override fun areItemsTheSame(
                    oldItem: CouponUi,
                    newItem: CouponUi,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: CouponUi,
                    newItem: CouponUi,
                ): Boolean = oldItem == newItem
            }
    }
}
