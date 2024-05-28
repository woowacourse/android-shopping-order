package woowacourse.shopping.presentation

import android.app.Application

class ShoppingApplication : Application() {
    override fun onTerminate() {
        super.onTerminate()
        DefaultShoppingProductService.shutdown()
    }
}
