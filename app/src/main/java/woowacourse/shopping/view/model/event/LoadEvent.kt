package woowacourse.shopping.view.model.event

sealed interface LoadEvent {
    data object Success : LoadEvent
    data object Loading: LoadEvent
    data class Fail(val event:ErrorEvent) : LoadEvent
}
