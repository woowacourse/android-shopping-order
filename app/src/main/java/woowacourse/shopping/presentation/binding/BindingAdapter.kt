package woowacourse.shopping.presentation.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("imageUrl")
fun setImageBindingAdapter(
    imageView: ImageView,
    imageUrl: String?,
) {
    Glide
        .with(imageView)
        .load(imageUrl)
        .into(imageView)
}

@BindingAdapter("startShimmer")
fun ShimmerFrameLayout.setShimmer(isLoading: Boolean) {
    if (isLoading) {
        startShimmer()
    } else {
        stopShimmer()
    }
}
