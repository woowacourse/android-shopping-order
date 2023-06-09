package woowacourse.shopping.presentation.ui.shoppingCart

import android.os.Handler
import android.os.Looper

class DebouncedTask(private val task: () -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { task() }

    fun performDebounceTask() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 500)
    }
}
