package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val userName: StateFlow<String?>

    val password: StateFlow<String?>

    suspend fun loadCredentialsToMemory()
}
