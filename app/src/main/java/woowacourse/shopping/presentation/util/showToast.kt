package woowacourse.shopping.presentation.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(
    messageResId: Int,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(this, getString(messageResId), duration).show()
}
