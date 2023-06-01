package woowacourse.shopping.view.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.repository.MypageRepository

class MypagePresenter(private val mypageRepository: MypageRepository) : MypageContract.Presenter {
    private val _cash = MutableLiveData(0)
    override val cash: LiveData<Int>
        get() = _cash

    override fun fetchCash() {
        mypageRepository.getCash {
            Log.d("get", it.toString())
            _cash.value = it
        }
    }

    override fun chargeCash(cash: Int) {
        mypageRepository.chargeCash(cash) {
            Log.d("post", it.toString())
            _cash.value = it
        }
    }
}
