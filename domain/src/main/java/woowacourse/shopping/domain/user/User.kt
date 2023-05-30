package woowacourse.shopping.domain.user

data class User(
    val id: Long,
    val name: String,
    val rank: Rank,
    val email: String,
    val password: String
)
