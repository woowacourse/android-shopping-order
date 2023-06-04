package woowacourse.shopping.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.ui.model.UiOrder
import woowacourse.shopping.ui.model.preorderinfo.PreOrderInfo
import woowacourse.shopping.ui.paymentconfirm.ApplyPointMessageCode

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
fun TextView.setPreOrderProductNameCount(preOrderInfo: PreOrderInfo) {
    text = if (preOrderInfo.representativeExceptCount > 0) {
        this.context.getString(R.string.tv_payment_product_name_count)
            .format(preOrderInfo.representativeTitle, preOrderInfo.representativeExceptCount)
    } else {
        preOrderInfo.representativeTitle
    }
}

@BindingAdapter("app:productNameCount")
fun TextView.setOrderProductNameCount(orderInfo: UiOrder) {
    val representativeExceptCount = (orderInfo.orderItems.size - 1)
    val representativeTitle = orderInfo.orderItems.first().product.name
    text = if (representativeExceptCount > 0) {
        this.context.getString(R.string.tv_order_item_on_image_count)
            .format(representativeTitle, representativeExceptCount)
    } else {
        representativeTitle
    }
}

@BindingAdapter("app:applyPointMessageCode")
fun TextView.setApplyPointMessageCode(applyPointMessageCode: ApplyPointMessageCode?) {
    if (applyPointMessageCode == null) {
        this.text = ""
        return
    }
    this.text = ApplyPointMessageCode.getMessage(this.context, applyPointMessageCode)
    when (applyPointMessageCode) {
        ApplyPointMessageCode.AVAILABLE_TO_APPLY -> {
            this.setTextColor(ContextCompat.getColor(this.context, R.color.woowa_button))
        }
        ApplyPointMessageCode.OVER_USE_POINT -> {
            this.setTextColor(ContextCompat.getColor(this.context, R.color.woowa_red))
        }
    }
}
