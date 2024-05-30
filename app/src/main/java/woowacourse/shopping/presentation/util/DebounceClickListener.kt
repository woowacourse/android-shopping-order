package woowacourse.shopping.presentation.util

import android.os.Handler
import android.os.Looper
import android.view.View
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * 클릭 이벤트를  debounce 하는 클래스
 * [interval] 시간 내에 여러번 클릭되는 경우, 가장 마지막 클릭 이벤트만 처리한다.
 *
 * button.setOnDebounceClickListener(300.toDuration(DurationUnit.MILLISECONDS)) { view ->
 *     // 클릭 이벤트 처리
 *     println("Button clicked!")
 * }
 *
 * @param interval 디바운스 시간
 * @param onDebounceClick 디바운스 이벤트
 */
class DebounceClickListener(
    private val interval: Duration,
    private val onDebounceClick: (View) -> Unit,
) : View.OnClickListener {
    private var lastClickTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    override fun onClick(view: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < interval.toLong(DurationUnit.MILLISECONDS)) {
            runnable?.let { handler.removeCallbacks(it) }
        }
        lastClickTime = currentTime
        runnable = Runnable { onDebounceClick(view) }
        handler.postDelayed(runnable!!, interval.toLong(DurationUnit.MILLISECONDS))
    }
}

fun View.setOnDebounceClickListener(
    interval: Duration,
    onDebounceClick: (View) -> Unit,
) {
    val debounceClickListener = DebounceClickListener(interval, onDebounceClick)
    setOnClickListener(debounceClickListener)
}
