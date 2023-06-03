package woowacourse.shopping.data.member

import woowacourse.shopping.data.server.MemberRemoteDataSource
import woowacourse.shopping.domain.repository.MemberRepository

class MemberRepositoryImpl(private val memberRemoteDataSource: MemberRemoteDataSource) : MemberRepository {
    override fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        memberRemoteDataSource.getPoints(onSuccess, onFailure)
    }
}