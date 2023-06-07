package woowacourse.shopping.feature.order.confirm

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.MoneySaleUiModel

interface OrderConfirmContract {
    interface View {
        fun showFailedLoadCartInfo()
        fun setSaleInfo(moneySaleUiModel: MoneySaleUiModel)
        fun setPayInfo(originMoney: Int, saleApplyMoney: Int)
        fun setFinalPayInfo(saleApplyMoney: Int)
        fun showSaleInfo()
        fun showNoneSaleInfo()
        fun showOrderSuccess(cartIds: List<Long>)
        fun showOrderFailed()
        fun showNetworkError()
        fun exitScreen()
    }

    interface Presenter {
        val cartProducts: LiveData<List<CartProductUiModel>>
        fun loadSelectedCarts()
        fun requestOrder()
    }
}
