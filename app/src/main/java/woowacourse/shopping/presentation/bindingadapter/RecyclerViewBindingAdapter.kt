package woowacourse.shopping.presentation.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.order.CouponAdapter

@BindingAdapter("coupons")
fun bindCouponList(
    view: RecyclerView,
    coupons: List<CouponUiModel>?,
) {
    val adapter = view.adapter
    if (adapter is CouponAdapter) {
        adapter.submitList(coupons ?: emptyList())
    }
}
