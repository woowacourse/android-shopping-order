package woowacourse.shopping.data.user

import woowacourse.shopping.Price

class UserRepositoryImpl(private val userDataSource: UserDataSource) : UserRepository {
    override fun getPoint(onSuccess: (Price) -> Unit, onFailure: () -> Unit) {
        userDataSource.getPoint({
            onSuccess(Price(it.result.totalPoint))
        }, {
            onFailure()
        })
    }
}
