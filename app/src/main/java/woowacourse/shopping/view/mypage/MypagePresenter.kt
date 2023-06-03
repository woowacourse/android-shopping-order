package woowacourse.shopping.view.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.repository.MypageRepository

class MypagePresenter(private val mypageRepository: MypageRepository) : MypageContract.Presenter {
    private val _cash = MutableLiveData(0)
    override val cash: LiveData<Int>
        get() = _cash

    override fun fetchCash() {
        mypageRepository.getCash {
            _cash.value = it
        }
    }

    override fun chargeCash(cash: Int) {
        mypageRepository.chargeCash(cash) {
            _cash.value = it
        }
    }
}
