package woowacourse.shopping.ui.orderdetail

import java.io.Serializable

interface OrderDetailNavigator : Serializable {

    fun navigateToShoppingView()

    fun navigateToPreviousView(onFailed: (errorMessage: String) -> Unit)
}
