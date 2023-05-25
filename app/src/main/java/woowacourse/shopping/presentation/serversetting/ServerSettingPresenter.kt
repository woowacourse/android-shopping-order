package woowacourse.shopping.presentation.serversetting

import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.repository.RecentProductRepository
import java.util.Base64

class ServerSettingPresenter constructor(
    private val sharedPreferencesDb: SharedPreferencesDb,
    private val recentProductRepository: RecentProductRepository
) : ServerContract.Presenter {
    override fun saveAuthToken() {
        sharedPreferencesDb.setString(
            AUTHORIZATION_TOKEN,
            encodeCredentialsToBase64(TEST_ID, TEST_PASSWORD)
        )
    }

    override fun saveBaseUrl(url: String) {
        ApiClient.baseUrl = url
    }

    override fun deleteCart() {
        recentProductRepository.deleteAllProducts()
    }

    private fun encodeCredentialsToBase64(userName: String, password: String): String {
        val credentials = "$userName:$password"
        val encoder: Base64.Encoder = Base64.getEncoder()
        return encoder.encodeToString(credentials.toByteArray())
    }

    companion object {
        const val AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN"

        private const val TEST_ID = "scott@gmail.com"
        private const val TEST_PASSWORD = "1234"
    }
}
