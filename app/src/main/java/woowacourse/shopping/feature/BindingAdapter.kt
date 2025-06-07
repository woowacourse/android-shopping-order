package woowacourse.shopping.feature

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.feature.cart.adapter.CartAdapter
import woowacourse.shopping.feature.cart.adapter.RecommendAdapter
import woowacourse.shopping.feature.goods.adapter.GoodsAdapter
import woowacourse.shopping.feature.model.GoodsItem
import woowacourse.shopping.feature.payment.adapter.PaymentCouponAdapter

@BindingAdapter("imgUrl")
fun ImageView.loadImageFromUrl(url: String?) {
    if (url != null && url.isNotEmpty()) {
        Glide
            .with(this.context)
            .load(url)
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_launcher_background)
    }
}

@BindingAdapter("cartItems")
fun RecyclerView.bindCartItems(items: List<CartProduct>?) {
    if (adapter is CartAdapter && items != null) {
        (adapter as CartAdapter).submitList(items)
    }
    if (adapter is RecommendAdapter && items != null) {
        (adapter as RecommendAdapter).submitList(items)
    }
}

@BindingAdapter("items")
fun RecyclerView.bindItems(items: List<GoodsItem>?) {
    if (adapter is GoodsAdapter && items != null) {
        (adapter as GoodsAdapter).setItems(items)
    }
}

@BindingAdapter("couponItems")
fun RecyclerView.bindCouponItems(items: List<Coupon>?) {
    if (adapter is PaymentCouponAdapter && items != null) {
        (adapter as PaymentCouponAdapter).submitList(items)
    }
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("count")
fun CustomCartQuantity.setQuantity(count: Int) {
    setCount(count)
}

@BindingAdapter("name")
fun CustomLastViewed.setName(name: String) {
    setName(name)
}
