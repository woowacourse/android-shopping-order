package woowacourse.shopping.view.mypage

import androidx.lifecycle.LiveData

interface MypageContract {
    interface View {
        fun showNegativeIntErrorToast()
        fun showNotSuccessfulErrorToast()
        fun showServerFailureToast()
    }
    interface Presenter {
        val cash: LiveData<Int>
        fun fetchCash()
        fun chargeCash(cash: Int)
    }
}
