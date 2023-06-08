package woowacourse.shopping.view.mypage

interface MyPageContract {
    interface View {
        var presenter: Presenter
        fun updatePointView(point: Int)
    }

    interface Presenter {
        fun getMemberInfo()
    }
}
