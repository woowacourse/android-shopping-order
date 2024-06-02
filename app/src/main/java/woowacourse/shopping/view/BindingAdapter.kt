package woowacourse.shopping.view

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import woowacourse.shopping.domain.model.CartItemCounter.Companion.DEFAULT_ITEM_COUNT

@BindingAdapter("bindingImageUrl")
fun setImageUrl(
    view: ImageView,
    url: String,
) {
    Glide.with(view.context)
        .load(url)
        .override(Target.SIZE_ORIGINAL)
        .into(view)
}

@BindingAdapter("visibleByItemCounter")
fun setVisibleByItemCounter(view: View, count: Int) {
    view.visibility = if (count > DEFAULT_ITEM_COUNT) View.VISIBLE else View.GONE
}
