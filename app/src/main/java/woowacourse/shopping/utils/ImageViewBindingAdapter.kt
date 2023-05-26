package woowacourse.shopping.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ImageViewBindingAdapter {
    @BindingAdapter("android:urlSrc")
    @JvmStatic
    fun src(imageView: ImageView, src: String?) {
        src ?: return
        Glide.with(imageView.context).load(src).centerCrop().into(imageView)
    }
}
