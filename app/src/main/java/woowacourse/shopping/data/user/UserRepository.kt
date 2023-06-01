package woowacourse.shopping.data.user

import woowacourse.shopping.Price

interface UserRepository {
    fun getPoint(onSuccess: (Price) -> Unit, onFailure: () -> Unit)
}
