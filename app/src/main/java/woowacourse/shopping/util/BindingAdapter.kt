package woowacourse.shopping.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.ui.model.PreOrderInfo

@BindingAdapter("app:image")
fun ImageView.setImage(imgUrl: String) {
    Glide.with(context)
        .load(imgUrl)
        .into(this)
}

@BindingAdapter("app:setThrottleFirstOnClick")
inline fun View.setBindingThrottleFirstOnClickListener(
    crossinline block: (View) -> Unit
) {
    val delay: Long = 50L
    var previousClickedTime = 0L
    setOnClickListener { view ->
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - previousClickedTime >= delay) {
            block(view)
            previousClickedTime = clickedTime
        }
    }
}

@BindingAdapter("app:productNameCount")
fun TextView.setProductNameCount(preOrderInfo: PreOrderInfo) {
    text = if (preOrderInfo.representativeExceptCount > 0) {
        this.context.getString(R.string.tv_payment_product_name_count)
            .format(preOrderInfo.representativeTitle, preOrderInfo.representativeExceptCount)
    } else {
        preOrderInfo.representativeTitle
    }
}
