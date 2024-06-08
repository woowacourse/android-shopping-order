package woowacourse.shopping.presentation.ui.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCouponBinding
import woowacourse.shopping.presentation.ui.model.CouponModel

class CouponViewHolder(
    private val binding: ItemCouponBinding,
    private val orderHandler: OrderHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponModel) {
        binding.coupon = item
        binding.orderHandler = orderHandler
    }
}
