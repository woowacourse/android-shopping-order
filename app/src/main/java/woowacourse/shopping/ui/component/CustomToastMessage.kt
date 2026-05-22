package woowacourse.shopping.ui.component

import android.content.Context
import android.widget.Toast

fun CustomToastMessage(
    context: Context,
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast
        .makeText(
            context,
            message,
            duration,
        ).show()
}