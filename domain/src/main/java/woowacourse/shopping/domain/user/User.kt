package woowacourse.shopping.domain.user

data class User(val id: Long, val rank: Rank, val email: String, val password: String)
