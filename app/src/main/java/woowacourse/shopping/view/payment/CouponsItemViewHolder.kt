package woowacourse.shopping.view.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.databinding.ItemCouponsHeaderBinding

sealed class CouponsItemViewHolder<I>(
    binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: I)

    class HeaderViewHolder(
        binding: ItemCouponsHeaderBinding,
    ) : CouponsItemViewHolder<CouponsItem.Header>(binding) {
        override fun bind(item: CouponsItem.Header) {
        }
    }

    class CouponViewHolder(
        private val binding: ItemCouponBinding,
        private val onSelect: (CouponsItem.CouponItem) -> Unit,
    ) : CouponsItemViewHolder<CouponsItem.CouponItem>(binding) {
        init {
            binding.onSelect = onSelect
        }

        override fun bind(item: CouponsItem.CouponItem) {
            binding.coupon = item
        }
    }

    companion object {
        fun of(
            viewType: CouponsItemViewType,
            parent: ViewGroup,
            onSelect: (CouponsItem.CouponItem) -> Unit,
        ): CouponsItemViewHolder<CouponsItem> {
            val layoutInflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                CouponsItemViewType.HEADER -> {
                    val binding = ItemCouponsHeaderBinding.inflate(layoutInflater, parent, false)
                    HeaderViewHolder(binding)
                }

                CouponsItemViewType.COUPON -> {
                    val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
                    CouponViewHolder(binding, onSelect)
                }
            } as CouponsItemViewHolder<CouponsItem>
        }
    }
}
