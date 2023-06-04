package woowacourse.shopping.presentation.ui.myPage

import woowacourse.shopping.domain.repository.ChargeRepository
import woowacourse.shopping.domain.util.WoowaResult

class MyPagePresenter(
    private val view: MyPageContract.View,
    private val chargeRepository: ChargeRepository,
) : MyPageContract.Presenter {

    override fun fetchCharge() {
        chargeRepository.fetchCharge { result ->
            when (result) {
                is WoowaResult.SUCCESS -> view.showCharge(result.data)
                is WoowaResult.FAIL -> view.showError()
            }
        }
    }

    override fun recharge(amount: Int) {
        chargeRepository.recharge(amount) { result ->
            when (result) {
                is WoowaResult.SUCCESS -> view.showCharge(result.data)
                is WoowaResult.FAIL -> view.showError()
            }
        }
    }
}
