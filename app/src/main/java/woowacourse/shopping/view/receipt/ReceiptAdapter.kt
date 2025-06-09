package woowacourse.shopping.view.receipt

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


class ReceiptAdapter : ListAdapter<CouponItem, ReceiptViewHolder>(CouponsItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        return ReceiptViewHolder.of(parent)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class CouponsItemDiffCallback : DiffUtil.ItemCallback<CouponItem>() {

    override fun areItemsTheSame(oldItem: CouponItem, newItem: CouponItem): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: CouponItem, newItem: CouponItem): Boolean {
        return oldItem == newItem
    }
}
