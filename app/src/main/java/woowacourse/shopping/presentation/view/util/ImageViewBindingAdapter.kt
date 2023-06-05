package woowacourse.shopping.presentation.view.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("setImageUrl")
fun setImageView(imageView: ImageView, imageUrl: String?) {
    imageUrl ?: return

    Picasso.get()
        .load(imageUrl)
        .fit()
        .centerCrop()
        .into(imageView)
}
