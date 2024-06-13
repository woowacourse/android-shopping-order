package woowacourse.shopping.presentation.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.presentation.ui.order.OrderEventHandler

class CouponsAdapter(private val handler: OrderEventHandler) :
    ListAdapter<CouponState, CouponsAdapter.CouponViewHolder>(CouponDiffCallback) {
    private var coupons: List<CouponState> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding, handler)
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

    fun loadData(coupons: List<CouponState>) {
        this.coupons = coupons
    }

    class CouponViewHolder(
        private val binding: ItemCouponBinding,
        private val actionHandler: OrderEventHandler,
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
