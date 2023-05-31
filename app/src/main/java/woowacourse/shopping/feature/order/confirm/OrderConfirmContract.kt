package woowacourse.shopping.feature.order.confirm

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartProductUiModel

interface OrderConfirmContract {
    interface View

    interface Presenter {
        val cartProducts: LiveData<List<CartProductUiModel>>
        fun loadSelectedCarts()
    }
}
