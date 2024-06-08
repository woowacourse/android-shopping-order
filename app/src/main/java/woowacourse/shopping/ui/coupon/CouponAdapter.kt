package woowacourse.shopping.ui.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.utils.ItemDiffCallback

class CouponAdapter(
    private val onClick: CouponClickListener,
) : ListAdapter<CouponUiModel, CouponAdapter.CouponViewHolder>(diffCallback) {
    class CouponViewHolder(
        private val binding: ItemCouponBinding,
        private val onClick: CouponClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CouponUiModel) {
            binding.coupon = item
            binding.couponClickListener = onClick
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.onBind(getItem(position))
    }

    companion object {
        private val diffCallback =
            ItemDiffCallback<CouponUiModel>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}
