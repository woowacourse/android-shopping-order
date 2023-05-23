package woowacourse.shopping

import android.app.Application

class App : Application() {

    companion object {
        var serverUrl = "http://localhost:3345"
    }
}
