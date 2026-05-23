package woowacourse.shopping.ui.component

import android.content.Context
import android.widget.Toast

fun customToastMessage(
    context: Context,
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast
        .makeText(
            context,
            message,
            duration,
        ).show()
}
