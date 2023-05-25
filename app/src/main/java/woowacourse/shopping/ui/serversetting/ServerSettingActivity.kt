package woowacourse.shopping.ui.serversetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.ui.serversetting.ServerSettingContract.Presenter
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.inject.inject
import woowacourse.shopping.util.preference.ShoppingPreference


class ServerSettingActivity : AppCompatActivity(), ServerSettingContract.View {
    private lateinit var binding: ActivityServerSettingBinding
    private val presenter: Presenter by lazy { inject(this, ShoppingPreference(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerSettingBinding.inflate(layoutInflater).setContentView(this)
        binding.presenter = presenter
    }

    override fun navigateToShopping(serverUrl: String) {
        startActivity(ShoppingActivity.getIntent(this, serverUrl))
        finish()
    }
}
