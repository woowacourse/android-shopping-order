package woowacourse.shopping.ui.orderdetail

import android.app.Activity
import android.content.Context
import woowacourse.shopping.ui.shopping.ShoppingActivity

class OrderDetailNavigatorImpl(
    private val context: Context,
) : OrderDetailNavigator {

    override fun navigateToShoppingView() {
        val intent = ShoppingActivity.getIntent(context)

        context.startActivity(intent)
    }

    override fun navigateToPreviousView(onFailed: (errorMessage: String) -> Unit) {
        when (context) {
            is Activity -> context.finish()
            else -> onFailed(FAILED_TO_NAVIGATING_PREVIOUS_VIEW)
        }
    }

    companion object {
        private const val FAILED_TO_NAVIGATING_PREVIOUS_VIEW = "이전 화면으로 전환할 수 없습니다."
    }
}
