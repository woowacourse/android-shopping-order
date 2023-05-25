package woowacourse.shopping.presentation.serversetting

interface ServerContract {
    interface Presenter {
        fun deleteCart()
        fun saveBaseUrl(url: String)
        fun saveAuthToken()
    }
}
