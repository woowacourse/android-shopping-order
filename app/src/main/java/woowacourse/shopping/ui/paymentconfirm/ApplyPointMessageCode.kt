package woowacourse.shopping.ui.paymentconfirm

import android.content.Context
import woowacourse.shopping.R

enum class ApplyPointMessageCode() {
    OVER_USE_POINT, AVAILABLE_TO_APPLY;

    companion object {
        fun getMessage(context: Context, applyPointMessageCode: ApplyPointMessageCode): String =
            when (applyPointMessageCode) {
                OVER_USE_POINT -> context.getString(R.string.ApplyPointMessageOverUsePoint)
                AVAILABLE_TO_APPLY -> context.getString(R.string.ApplyPointMessageAvailableToApply)
            }
    }
}
