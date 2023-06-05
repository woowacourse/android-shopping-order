package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.User
import java.util.concurrent.CompletableFuture

interface UserRepository {

    fun getUser(): CompletableFuture<Result<User>>
}
