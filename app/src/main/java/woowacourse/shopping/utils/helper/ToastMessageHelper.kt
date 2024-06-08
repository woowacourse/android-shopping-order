package woowacourse.shopping.utils.helper

import android.content.Context
import android.widget.Toast

object ToastMessageHelper {
    fun Context.makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
