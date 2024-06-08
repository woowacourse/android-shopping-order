package woowacourse.shopping.presentation.order.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.util.ItemDiffCallback

class CouponAdapter(
    private val listener: CouponClickListener,
) : ListAdapter<CouponUiModel, CouponAdapter.CouponViewHolder>(couponComparator) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, listener)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class CouponViewHolder(
        private val binding: ItemCouponBinding,
        private val listener: CouponClickListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: CouponUiModel) {
            binding.coupon = coupon
            binding.listener = listener
        }
    }

    companion object {
        val couponComparator =
            ItemDiffCallback<CouponUiModel>(
                onItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
                onContentsTheSame = { oldItem, newItem -> oldItem == newItem },
            )
    }
}
