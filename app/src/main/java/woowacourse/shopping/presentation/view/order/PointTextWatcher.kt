package woowacourse.shopping.presentation.view.order

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import woowacourse.shopping.presentation.model.PointModel

class PointTextWatcher(
    private val orderPrice: Int,
    private val userPoint: PointModel,
    private val editText: EditText,
    private val onUsePointChange: (Int) -> Unit,
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            if (it.isEmpty()) return@let

            var primitivePrice = primitivePrice(it.toString())
            val price = if (primitivePrice.isEmpty()) 0 else primitivePrice.toInt()

            if (price > userPoint.value) {
                primitivePrice = userPoint.value.toString()
            }

            if (primitivePrice.toInt() > orderPrice) {
                primitivePrice = orderPrice.toString()
            }

            val formattingPrice = formatPrice(primitivePrice)
            onUsePointChange(price)
            editText.removeTextChangedListener(this)
            editText.setText(formattingPrice)
            editText.setSelection((editText.text.toString().length - " 원".length).coerceAtLeast(0))
            editText.addTextChangedListener(this)
        }
    }

    private fun primitivePrice(formattingPrice: String): String =
        formattingPrice.substringBefore("원").trim().replace(",", "")

    private fun formatPrice(price: String): String {
        if (price.isEmpty())
            return ""
        return String.format("%,d 원", price.toInt())
    }
}
