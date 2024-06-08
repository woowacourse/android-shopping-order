package woowacourse.shopping.presentation.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.Coupon

class CouponAdapter : ListAdapter<Coupon, CouponViewHolder>(CouponAdapterDiffCallback) {
    companion object {
        private val CouponAdapterDiffCallback =
            ItemDiffCallback<Coupon>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new },
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

class ItemDiffCallback<T : Any>(
    val onItemsTheSame: (T, T) -> Boolean,
    val onContentsTheSame: (T, T) -> Boolean,
) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(
        oldItem: T,
        newItem: T,
    ): Boolean = onItemsTheSame(oldItem, newItem)

    override fun areContentsTheSame(
        oldItem: T,
        newItem: T,
    ): Boolean = onContentsTheSame(oldItem, newItem)
}

class CouponViewHolder(
    private val binding: ItemCouponBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Coupon) {
        binding.coupon = item
//        binding.handler
    }
}
