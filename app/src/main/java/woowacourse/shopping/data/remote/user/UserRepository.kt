package woowacourse.shopping.data.remote.user

import woowacourse.shopping.Price

interface UserRepository {
    fun getPoint(onSuccess: (Price) -> Unit, onFailure: () -> Unit)
}
