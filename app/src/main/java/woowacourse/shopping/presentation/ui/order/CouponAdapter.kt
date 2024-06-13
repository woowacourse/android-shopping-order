package woowacourse.shopping.presentation.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.ui.model.CouponModel

class CouponAdapter(
    private val coupons: List<CouponModel> = emptyList(),
    private val orderHandler: OrderHandler,
) : ListAdapter<CouponModel, CouponViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CouponViewHolder {
        val view = ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponViewHolder(view, orderHandler)
    }

    override fun onBindViewHolder(
        holder: CouponViewHolder,
        position: Int,
    ) {
        val coupon = getItem(position)
        holder.bind(coupon)
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CouponModel>() {
                override fun areItemsTheSame(
                    oldCartItem: CouponModel,
                    newCartItem: CouponModel,
                ): Boolean {
                    return oldCartItem.id == newCartItem.id
                }

                override fun areContentsTheSame(
                    oldCartItem: CouponModel,
                    newCartItem: CouponModel,
                ): Boolean {
                    return oldCartItem == newCartItem
                }
            }
    }
}
