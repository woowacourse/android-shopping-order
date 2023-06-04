package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.ui.model.mapper.OrderMapper.toView

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val memberRepository: MemberRepository
) : OrderDetailContract.Presenter {
    override fun loadDetail(id: Int) {
        memberRepository.getOrder(
            id,
            onSuccess = {
                view.showDetail(it.toView())
            },
            onFailure = {
                view.notifyLoadFailed()
            }
        )

    }
}