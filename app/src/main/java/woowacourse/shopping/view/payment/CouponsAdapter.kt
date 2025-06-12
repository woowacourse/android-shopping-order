package woowacourse.shopping.view.payment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CouponsAdapter(
    private val onSelect: (CouponsItem.CouponItem) -> Unit,
) : ListAdapter<CouponsItem, CouponsItemViewHolder<CouponsItem>>(diffUtil) {
    override fun getItemViewType(position: Int): Int = currentList[position].viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponsItemViewHolder<CouponsItem> {
        val viewType: CouponsItemViewType = CouponsItemViewType.entries[viewType]
        return CouponsItemViewHolder.of(viewType, parent, onSelect)
    }

    override fun onBindViewHolder(
        holder: CouponsItemViewHolder<CouponsItem>,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<CouponsItem>() {
                override fun areItemsTheSame(
                    oldItem: CouponsItem,
                    newItem: CouponsItem,
                ): Boolean =
                    when (oldItem) {
                        is CouponsItem.CouponItem ->
                            oldItem.value.id == (newItem as? CouponsItem.CouponItem)?.value?.id

                        CouponsItem.Header -> oldItem.viewType == newItem.viewType
                    }

                override fun areContentsTheSame(
                    oldItem: CouponsItem,
                    newItem: CouponsItem,
                ): Boolean = oldItem == newItem
            }
    }
}
