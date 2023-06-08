package woowacourse.shopping.view.mypage

import woowacourse.shopping.server.retrofit.MembersService
import woowacourse.shopping.server.retrofit.createResponseCallback

class MyPagePresenter(
    private val view: MyPageContract.View,
    private val service: MembersService
) : MyPageContract.Presenter {
    override fun getMemberInfo() {
        service.getPoint().enqueue(
            createResponseCallback(
                onSuccess = { member ->
                    view.updatePointView(member.point)
                },
                onFailure = {
                    throw IllegalStateException(NOT_FOUNT_POINT_ERROR)
                }
            )
        )
    }

    companion object {
        private const val NOT_FOUNT_POINT_ERROR = "포인트를 불러오는데 실패하였습니다."
    }
}
