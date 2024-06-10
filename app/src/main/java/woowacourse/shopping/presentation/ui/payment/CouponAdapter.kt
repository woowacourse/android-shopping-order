package woowacourse.shopping.presentation.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.coupon.Coupon

class CouponAdapter(
    private val couponActionHandler: CouponActionHandler,
) : ListAdapter<Coupon, CouponViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding, couponActionHandler)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        private val DIFF_CALLBACK =
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
            }
    }
}

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val couponActionHandler: CouponActionHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Coupon) {
        binding.coupon = item
        binding.couponActionHandler = couponActionHandler
    }
}
