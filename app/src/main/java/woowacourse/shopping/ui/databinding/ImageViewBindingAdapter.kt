package woowacourse.shopping.ui.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ImageViewBindingAdapter {
    @BindingAdapter("app:urlSrc")
    @JvmStatic
    fun src(imageView: ImageView, src: String?) {
        src ?: return
        Glide.with(imageView.context).load(src).centerCrop().into(imageView)
    }
}
