package woowacourse.shopping.ui.orderdetail

import android.app.Activity
import android.content.Context
import woowacourse.shopping.ui.model.OrderUiModel
import woowacourse.shopping.ui.shopping.ShoppingActivity

sealed class OrderDetailNavigation(protected val context: Context) {

    abstract fun navigate()

    class ShoppingViewCase(
        context: Context,
        val orderId: Int,
    ) : OrderDetailNavigation(context) {

        override fun navigate() {
            val intent = ShoppingActivity.getIntent(context)

            context.startActivity(intent)
        }
    }

    class PreviousViewCase(
        context: Context,
        val order: OrderUiModel,
    ) : OrderDetailNavigation(context) {

        override fun navigate() {
            (context as? Activity)?.finish()
        }
    }

    companion object {

        fun valueOf(
            context: Context,
            orderId: Int = -1,
            order: OrderUiModel? = null,
        ): OrderDetailNavigation {

            return when {
                order != null -> PreviousViewCase(context, order)
                else -> ShoppingViewCase(context, orderId)
            }
        }
    }
}
