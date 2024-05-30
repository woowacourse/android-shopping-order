package woowacourse.shopping.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content

    fun isActive(): Boolean = !hasBeenHandled
}

fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    observer: Observer<T>,
) = observe(owner) {
    it?.let {
        if (it.isActive()) {
            observer.onChanged(it.peekContent())
        }
    }
}
