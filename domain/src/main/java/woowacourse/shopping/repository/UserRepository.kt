package woowacourse.shopping.repository

import woowacourse.shopping.domain.user.User
import java.util.concurrent.CompletableFuture

interface UserRepository {
    fun saveCurrent(user: User)
    fun findAll(): CompletableFuture<Result<List<User>>>
    fun findCurrent(): CompletableFuture<Result<User>>
}
