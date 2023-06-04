package woowacourse.shopping.data.member

import woowacourse.shopping.data.server.MemberRemoteDataSource
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory
import woowacourse.shopping.domain.repository.MemberRepository

class MemberRepositoryImpl(private val memberRemoteDataSource: MemberRemoteDataSource) : MemberRepository {
    override fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        memberRemoteDataSource.getPoints(onSuccess, onFailure)
    }

    override fun getOrderHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit) {
        memberRemoteDataSource.getOrderHistories(onSuccess, onFailure)
    }

    override fun getOrder(id: Int, onSuccess: (Order) -> Unit, onFailure: () -> Unit) {
        memberRemoteDataSource.getOrder(id, onSuccess, onFailure)
    }
}