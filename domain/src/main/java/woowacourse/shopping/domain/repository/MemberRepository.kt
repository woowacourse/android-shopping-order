package woowacourse.shopping.domain.repository

interface MemberRepository {
    fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit)
}