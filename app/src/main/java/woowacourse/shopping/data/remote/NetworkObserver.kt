package woowacourse.shopping.data.remote

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {
    fun observeNetwork(): Flow<Boolean>
}
