package woowacourse.shopping.utils

import android.app.Activity
import android.util.Log

object ActivityUtils {
    fun keyError(activity: Activity, key: String) {
        Log.e("Intent", "Intent에 $key 키가 없습니다.")
        activity.finish()
    }

    fun showErrorMessage(message: String?) {
        Log.e("Error", "오류가 발생했습니다. $message")
    }
}
