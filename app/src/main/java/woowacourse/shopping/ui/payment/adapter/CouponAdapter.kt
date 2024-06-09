package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewmodel

class CouponAdapter(val viewModel: PaymentViewmodel) :
    ListAdapter<CouponState, CouponViewHolder>(diffUtil) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        return CouponViewHolder(
            ItemCouponBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position], viewModel)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<CouponState>() {
                override fun areContentsTheSame(
                    oldItem: CouponState,
                    newItem: CouponState,
                ) = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: CouponState,
                    newItem: CouponState,
                ) = oldItem.coupon.id == newItem.coupon.id
            }
    }
}
