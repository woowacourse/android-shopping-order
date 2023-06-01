package woowacourse.shopping.view.mypage

import androidx.lifecycle.LiveData

interface MypageContract {
    interface Presenter {
        val cash: LiveData<Int>
        fun fetchCash()
        fun chargeCash(cash: Int)
    }
}
