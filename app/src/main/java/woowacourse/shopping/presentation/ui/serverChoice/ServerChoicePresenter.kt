package woowacourse.shopping.presentation.ui.serverChoice

import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.domain.repository.AuthRepository

class ServerChoicePresenter(
    private val authRepository: AuthRepository,
    private val view: ServerChoiceContract.View,
) :
    ServerChoiceContract.Presenter {
    init {
        authRepository.setToken("Basic Yml4eEBiaXh4LmNvbToxMjM0")
    }

    override fun setServer(url: ServicePool.UrlPool) {
        view.setServer(url, authRepository.getToken() ?: "")
    }
}
