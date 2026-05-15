package woowacourse.shopping.data.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkStatusMonitor {
    val isConnected: StateFlow<Boolean>
}
