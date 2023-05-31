package woowacourse.shopping.ui.payment

import android.text.Editable
import android.text.TextWatcher
import java.lang.Integer.min

class InputPointWatcher(
    private val totalPrice: Int,
    private val possessingPoint: Int,
    private val availableCase: (price: Int) -> Unit,
    private val inAvailableCase: (price: Int) -> Unit,
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(
        inputPoint: Editable?,
    ) {
        if (!inputPoint.isNullOrEmpty()) {
            val usingPoint = inputPoint.toString().toInt()
            val isAvailableToUse = usingPoint <= min(totalPrice, possessingPoint)

            if (isAvailableToUse) {
                availableCase(totalPrice - usingPoint)
            } else {
                inAvailableCase(totalPrice)
            }
        }
    }
}
