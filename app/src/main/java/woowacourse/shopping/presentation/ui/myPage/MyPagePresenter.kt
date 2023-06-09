package woowacourse.shopping.presentation.ui.myPage

import woowacourse.shopping.domain.repository.ChargeRepository

class MyPagePresenter(
    private val view: MyPageContract.View,
    private val chargeRepository: ChargeRepository,
) : MyPageContract.Presenter {

    override fun fetchCharge() {
        chargeRepository.fetchCharge { result: Result<Int> ->
            result
                .onSuccess { view.showCharge(it) }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
    }

    override fun recharge(amount: Int) {
        chargeRepository.recharge(amount) { result: Result<Int> ->
            result
                .onSuccess { view.showCharge(it) }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
    }
}
