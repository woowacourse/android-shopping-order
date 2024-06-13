package woowacourse.shopping.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.ui.model.CouponUiModel
import woowacourse.shopping.ui.payment.OnCouponClickListener

class CouponAdapter(
    private val onCouponClickListener: OnCouponClickListener,
) : ListAdapter<CouponUiModel, CouponViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCouponBinding.inflate(inflater, parent, false)
        return CouponViewHolder(binding, onCouponClickListener)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateCoupons(newCoupons: List<CouponUiModel>) {
        submitList(newCoupons)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<CouponUiModel>() {
                override fun areItemsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: CouponUiModel,
                    newItem: CouponUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}
