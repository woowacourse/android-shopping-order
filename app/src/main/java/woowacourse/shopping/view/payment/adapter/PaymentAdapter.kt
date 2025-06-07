package woowacourse.shopping.view.payment.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.domain.model.coupon.Coupon

class PaymentAdapter() : ListAdapter<Coupon, PaymentViewHolder>(
    object : DiffUtil.ItemCallback<Coupon>() {
        override fun areItemsTheSame(
            oldItem: Coupon,
            newItem: Coupon,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Coupon,
            newItem: Coupon,
        ): Boolean {
            return oldItem == newItem
        }
    },
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PaymentViewHolder = PaymentViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: PaymentViewHolder,
        position: Int,
    ) {
        Log.d("hwannow_log", "onBindViewHolder: ")
        holder.bind(getItem(position))
    }
}
