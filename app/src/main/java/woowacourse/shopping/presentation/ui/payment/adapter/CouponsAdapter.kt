package woowacourse.shopping.presentation.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCouponBinding
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.presentation.ui.payment.PaymentActionHandler

class CouponsAdapter(private val actionHandler: PaymentActionHandler) :
    ListAdapter<CouponState, CouponsAdapter.CouponViewHolder>(CouponDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HolderCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding, actionHandler)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class CouponViewHolder(
        private val binding: HolderCouponBinding,
        private val actionHandler: PaymentActionHandler,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(couponState: CouponState) {
            binding.couponState = couponState
            binding.actionHandler = actionHandler
            binding.executePendingBindings()
        }
    }

    object CouponDiffCallback : DiffUtil.ItemCallback<CouponState>() {
        override fun areItemsTheSame(
            oldItem: CouponState,
            newItem: CouponState,
        ): Boolean {
            return oldItem.coupon.id == newItem.coupon.id
        }

        override fun areContentsTheSame(
            oldItem: CouponState,
            newItem: CouponState,
        ): Boolean {
            return oldItem.coupon == newItem.coupon
        }
    }
}
