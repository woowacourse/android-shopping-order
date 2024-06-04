package woowacourse.shopping.presentation.ui.shopping.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("productUrl")
fun ImageView.setProductUrl(url: String?) {
    url?.let {
        Glide.with(this.context)
            .load(it)
            .into(this)
    }
}
