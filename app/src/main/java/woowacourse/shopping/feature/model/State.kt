package woowacourse.shopping.feature.model

sealed class State {
    object Success : State()

    sealed class Failure : State() {
        data object BadRequest : Failure()

        data object NetworkError : Failure()
    }
}
