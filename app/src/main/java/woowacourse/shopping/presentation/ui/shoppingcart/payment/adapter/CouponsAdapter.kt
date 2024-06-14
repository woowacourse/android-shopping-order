package woowacourse.shopping.presentation.ui.shoppingcart.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCouponBinding
import woowacourse.shopping.presentation.ui.shoppingcart.payment.CouponUiState

class CouponsAdapter(
    private val couponListActionHandler: CouponListActionHandler,
) : ListAdapter<CouponUiState, CouponsAdapter.CouponViewHolder>(CouponDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HolderCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        postion: Int,
    ) {
        holder.bind(getItem(postion), couponListActionHandler)
    }

    class CouponViewHolder(
        private val binding: HolderCouponBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            couponUiState: CouponUiState,
            couponListActionHandler: CouponListActionHandler,
        ) {
            binding.couponUiState = couponUiState
            binding.action = couponListActionHandler
        }
    }

    object CouponDiffCallback : DiffUtil.ItemCallback<CouponUiState>() {
        override fun areItemsTheSame(
            oldItem: CouponUiState,
            newItem: CouponUiState,
        ): Boolean {
            return oldItem.coupon == newItem.coupon
        }

        override fun areContentsTheSame(
            oldItem: CouponUiState,
            newItem: CouponUiState,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
