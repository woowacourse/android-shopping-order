package woowacourse.shopping.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConnectivityManagerNetworkMonitor(
    context: Context,
) : NetworkMonitor,
    AutoCloseable {
    private val connectivityManager =
        context.applicationContext.getSystemService(ConnectivityManager::class.java)

    private val _isNetworkConnected = MutableStateFlow(currentConnectionState())
    override val isNetworkConnected: StateFlow<Boolean> = _isNetworkConnected.asStateFlow()

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkConnected.value = currentConnectionState()
            }

            override fun onLost(network: Network) {
                _isNetworkConnected.value = currentConnectionState()
            }

            override fun onUnavailable() {
                _isNetworkConnected.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                _isNetworkConnected.value = networkCapabilities.isInternetAvailable()
            }
        }

    init {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback,
        )
    }

    private fun currentConnectionState(): Boolean {
        val currentNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return false

        return capabilities.isInternetAvailable()
    }

    private fun NetworkCapabilities.isInternetAvailable(): Boolean =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    override fun close() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
