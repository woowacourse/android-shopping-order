package woowacourse.shopping.presentation.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.cart.CartAdapter
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.order.CouponAdapter
import woowacourse.shopping.presentation.product.RecentAdapter

@BindingAdapter("coupons")
fun bindCoupons(
    view: RecyclerView,
    coupons: List<CouponUiModel>?,
) {
    val adapter = view.adapter
    if (adapter is CouponAdapter) {
        adapter.submitList(coupons ?: emptyList())
    }
}

@BindingAdapter("recentProducts")
fun bindRecentProducts(
    view: RecyclerView,
    recentProducts: List<Product>?,
) {
    val adapter = view.adapter
    if (adapter is RecentAdapter) {
        adapter.submitList(recentProducts ?: emptyList())
    }
}

@BindingAdapter("cartItems")
fun bindCartItems(
    view: RecyclerView,
    cartItems: List<CartItemUiModel>?,
) {
    val adapter = view.adapter
    if (adapter is CartAdapter) {
        adapter.submitList(cartItems ?: emptyList())
    }
}
