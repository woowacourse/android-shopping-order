package woowacourse.shopping.ui.utils.bindingAdapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.ui.utils.extension.urlToImage

@BindingAdapter("imageUrl")
fun ImageView.bindUrlToImage(imageUrl: String?) {
    urlToImage(context, imageUrl)
}
