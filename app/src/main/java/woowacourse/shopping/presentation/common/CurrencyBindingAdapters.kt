package woowacourse.shopping.presentation.common

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.OrderCartsUiState
import java.text.NumberFormat
import java.util.Locale

@BindingAdapter("priceToCurrency")
fun TextView.bindPriceToCurrency(price: Int?) {
    price?.let { priceValue ->
        this.text = priceValue.currency(context)
    }
}

@BindingAdapter("castToPriceToCurrency")
fun TextView.bindCastToPriceToCurrency(orderCartsUiState: OrderCartsUiState) {
    text =
        if (orderCartsUiState is OrderCartsUiState.Success) {
            orderCartsUiState.orderTotalPrice.currency(context)
        } else {
            ""
        }
}

fun Int.currency(context: Context): String {
    return when (Locale.getDefault().country) {
        Locale.KOREA.country -> context.getString(R.string.price_format_kor, this)
        else -> NumberFormat.getCurrencyInstance(Locale.getDefault()).format(this)
    }
}
