package woowacourse.shopping.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.product.catalog.ProductUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(productUiModel: ProductUiModel) {
        Glide
            .with(this)
            .load(productUiModel.imageUrl)
            .placeholder(R.drawable.iced_americano)
            .fallback(R.drawable.iced_americano)
            .error(R.drawable.iced_americano)
            .into(this)
    }

    private val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    @JvmStatic
    @BindingAdapter("couponExpirationDate")
    fun setCouponExpirationDate(textView: TextView, date: LocalDate?) {
        date?.let {
            val context = textView.context
            val formattedDate = it.format(formatter)
            val finalText = context.getString(R.string.coupon_expiration_date, formattedDate)
            textView.text = finalText
        }
    }
}
