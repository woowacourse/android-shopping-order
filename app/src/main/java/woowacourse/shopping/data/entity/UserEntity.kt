package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass
import woowacourse.shopping.domain.user.Rank
import woowacourse.shopping.domain.user.User
import java.util.Base64

@JsonClass(generateAdapter = true)
data class UserEntity(
    val id: Long,
    val email: String,
    val password: String,
    val grade: String
) {
    companion object {
        fun UserEntity.toDomain(): User {
            return User(
                id,
                email,
                password,
                Base64.getEncoder().encodeToString("$email:$password".toByteArray()),
                Rank.valueOf(grade)
            )
        }

        fun User.toEntity(): UserEntity {
            return UserEntity(
                id, email, password, rank.toString()
            )
        }
    }
}
