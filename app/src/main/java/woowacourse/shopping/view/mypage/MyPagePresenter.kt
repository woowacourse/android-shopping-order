package woowacourse.shopping.view.mypage

import com.shopping.repository.MemberRepository

class MyPagePresenter(
    private val view: MyPageContract.View,
    private val memberRepository: MemberRepository
) : MyPageContract.Presenter {

    override fun getMemberInfo() {
        memberRepository.getPoint(
            onSuccess = { member ->
                view.updatePointView(member.point)
            }
        )
    }
}
