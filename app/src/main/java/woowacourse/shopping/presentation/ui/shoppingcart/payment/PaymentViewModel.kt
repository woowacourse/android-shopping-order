package woowacourse.shopping.presentation.ui.shoppingcart.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.emit

class PaymentViewModel : BaseViewModel() {
    private val _navigateAction: MutableLiveData<Event<PaymentNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<PaymentNavigateAction>> get() = _navigateAction

    override fun retry() {}

    fun makeAPayment() {
        _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
    }

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                PaymentViewModel()
            }
        }
    }
}
