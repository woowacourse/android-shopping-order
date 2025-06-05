package woowacourse.shopping.presentation.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.event.CartEventHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.recommend.RecommendAdapter

@BindingAdapter("loadImage")
fun loadImage(
    imageView: ImageView,
    imageUrl: String?,
) {
    Glide
        .with(imageView)
        .load(imageUrl)
        .placeholder(R.drawable.iced_americano)
        .fallback(R.drawable.iced_americano)
        .error(R.drawable.iced_americano)
        .into(imageView)
}

@BindingAdapter("pageNumber")
fun setPageNumber(
    view: TextView,
    handler: CartEventHandler,
) {
    view.text = (handler.getPage() + 1).toString()
}

@BindingAdapter("enableNextButton")
fun setNextButtonEnabled(
    view: View,
    handler: CartEventHandler,
) {
    view.isEnabled = handler.isNextButtonEnabled()
}

@BindingAdapter("enablePrevButton")
fun setPrevButtonEnabled(
    view: View,
    handler: CartEventHandler,
) {
    view.isEnabled = handler.isPrevButtonEnabled()
}

@BindingAdapter("app:visibleIfNotSameProduct")
fun View.visibleIfNotSameProduct(
    recentItem: ProductUiModel?,
    current: ProductUiModel?,
) {
    visibility = if (recentItem != null && recentItem.id != current?.id) View.VISIBLE else View.GONE
}

@BindingAdapter("recommendProducts")
fun RecyclerView.setRecommendProducts(products: List<ProductUiModel>?) {
    (adapter as? RecommendAdapter)?.setItems(products.orEmpty())
}
