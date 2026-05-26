package woowacourse.shopping.data.remote.common

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val isNetworkConnected: StateFlow<Boolean>
}
