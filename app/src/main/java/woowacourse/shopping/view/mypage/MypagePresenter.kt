package woowacourse.shopping.view.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.MypageRepository

class MypagePresenter(private val view: MypageContract.View, private val mypageRepository: MypageRepository) : MypageContract.Presenter {
    private val _cash = MutableLiveData(0)
    override val cash: LiveData<Int>
        get() = _cash

    override fun fetchCash() {
        mypageRepository.getCash { result ->
            when (result) {
                is DataResult.Success -> {
                    _cash.value = result.response
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }

    override fun chargeCash(cash: Int) {
        if (cash < 0) {
            view.showNegativeIntErrorToast()
            return
        }
        mypageRepository.chargeCash(cash) { result ->
            when (result) {
                is DataResult.Success -> {
                    _cash.value = result.response
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }
}
