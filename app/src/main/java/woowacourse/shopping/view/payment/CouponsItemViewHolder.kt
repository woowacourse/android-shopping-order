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
        binding: ItemCouponBinding,
    ) : CouponsItemViewHolder<CouponsItem.Coupon>(binding) {
        override fun bind(item: CouponsItem.Coupon) {
        }
    }

    companion object {
        fun of(
            viewType: CouponsItemViewType,
            parent: ViewGroup,
        ): CouponsItemViewHolder<CouponsItem> {
            val layoutInflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                CouponsItemViewType.HEADER -> {
                    val binding = ItemCouponsHeaderBinding.inflate(layoutInflater, parent, false)
                    HeaderViewHolder(binding)
                }

                CouponsItemViewType.COUPON -> {
                    val binding = ItemCouponBinding.inflate(layoutInflater, parent, false)
                    CouponViewHolder(binding)
                }
            } as CouponsItemViewHolder<CouponsItem>
        }
    }
}
