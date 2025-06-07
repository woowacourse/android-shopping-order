package woowacourse.shopping.view.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
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
fun TextView.setExpirationDate(date: String) {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    val date = LocalDate.parse(date, inputFormatter)
    text = context.getString(R.string.coupon_expiration_date, date.format(outputFormatter))
}

@BindingAdapter("minimumAmount")
fun TextView.setMinimumAmount(amount: Int?) {
    text = amount?.let {
        context.getString(R.string.coupon_minimum_amount, it)
    } ?: ""
}

@BindingAdapter("deliveryFee")
fun TextView.setDeliveryFee(fee: Int) {
    text =
        if (fee == 0) {
            context.getString(R.string.free_delivery_fee)
        } else {
            context.getString(R.string.product_price, fee)
        }
}
