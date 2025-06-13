package woowacourse.shopping.view.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.Discount
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("imageUrl")
fun ImageView.setImage(image: String?) {
    image ?: return
    Glide
        .with(this.context)
        .load(image)
        .placeholder(R.drawable.ic_launcher_foreground)
        .centerCrop()
        .into(this)
}

@BindingAdapter("expirationDate")
fun TextView.setExpirationDate(date: LocalDate?) {
    date ?: return

    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
    this.text = context.getString(R.string.expiration_date, date.format(formatter))
}

@BindingAdapter("minimumAmount")
fun TextView.setMinimumAmount(discount: Discount?) {
    discount ?: return
    val amount =
        when (discount) {
            is Discount.FixedAmount -> discount.minimumAmount
            is Discount.BuyXGetYFree -> null
            is Discount.FreeShipping -> discount.minimumAmount
            is Discount.Percentage -> null
        }
    amount ?: return
    this.text = context.getString(R.string.minimum_amount, amount)
}
