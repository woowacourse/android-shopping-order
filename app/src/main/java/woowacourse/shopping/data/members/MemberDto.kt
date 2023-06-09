package woowacourse.shopping.data.members

data class MemberDto(
    val id: Long,
    val email: String,
    val password: String,
    val grade: String
)
