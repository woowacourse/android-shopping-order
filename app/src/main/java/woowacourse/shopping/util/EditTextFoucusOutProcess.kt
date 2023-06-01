package woowacourse.shopping.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.editTextFocusOutProcess(ev: MotionEvent?) {
    val focusView = currentFocus
    if (focusView != null && ev != null) {
        val rect = Rect()
        focusView.getGlobalVisibleRect(rect)
        val x = ev.x.toInt()
        val y = ev.y.toInt()

        if (!rect.contains(x, y)) {
            val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
            focusView.clearFocus()
        }
    }
}
