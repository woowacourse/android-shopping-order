package woowacourse.shopping.view.core.ext

import android.content.Intent
import android.os.Build
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Serializable> Intent.getSerializableArrayList(key: String): ArrayList<T> {
    return (
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(key, ArrayList::class.java)
        } else {
            @Suppress("DEPRECATION")
            getSerializableExtra(key)
        }
    ) as ArrayList<T>
}
