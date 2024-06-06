package woowacourse.shopping.presentation.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R

@BindingAdapter("app:imageUrl")
fun loadImage(
    view: ImageView,
    url: String?,
) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.color.shimmer_content_color)
            .error(R.color.shimmer_content_color)
            .into(view)
    }
}

@BindingAdapter("app:price")
fun setPrice(
    view: TextView,
    price: Long?,
) {
    view.text = price?.let { view.context.getString(R.string.price_format, it) } ?: ""
}

@BindingAdapter("app:visibility")
fun setLoadMoreBtnVisibility(
    view: TextView,
    isVisible: Boolean?,
) {
    if (isVisible == true) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("app:shoppingCounterVisibility")
fun setVisibility(
    view: View,
    isVisible: Boolean?,
) {
    if (isVisible == true) {
        if (view is ImageView) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    } else {
        if (view is ImageView) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("app:cartViewVisibility")
fun setViewVisibility(
    view: View,
    isVisible: Boolean?,
) {
    if (isVisible == true) {
        if (view is TextView) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    } else {
        if (view is TextView) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}
