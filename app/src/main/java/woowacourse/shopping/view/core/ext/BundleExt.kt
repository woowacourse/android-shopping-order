package woowacourse.shopping.view.core.ext

import android.content.Intent
import android.os.Build
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getSerializableObject(key: String): T {
    val value =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getSerializableExtra(key)
        }

    return requireNotNull(value as? T) {
        "Intent extra '$key' is not of expected type ${T::class.java.name}"
    }
}
