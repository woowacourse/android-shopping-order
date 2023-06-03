package woowacourse.shopping.data.server

interface MemberRemoteDataSource {
    fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit)
}