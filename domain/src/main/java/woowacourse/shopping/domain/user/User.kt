package woowacourse.shopping.domain.user

data class User(
    val id: Long,
    val email: String,
    val password: String,
    val token: String,
    val rank: Rank
)
