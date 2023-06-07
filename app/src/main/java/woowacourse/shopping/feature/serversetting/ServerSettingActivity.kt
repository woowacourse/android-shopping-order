package woowacourse.shopping.feature.serversetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.preferences.UserPreference
import woowacourse.shopping.databinding.ActivityServerSettingBinding
import woowacourse.shopping.feature.main.MainActivity
import woowacourse.shopping.util.showToastShort

class ServerSettingActivity : AppCompatActivity(), ServerContract.View {
    private lateinit var binding: ActivityServerSettingBinding
    private lateinit var presenter: ServerContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_setting)
        presenter = ServerSettingPresenter(this, UserPreference)
        binding.presenter = presenter
    }

    override fun showMainScreen(serverName: String) {
        showToastShort(R.string.enter_server_text, serverName)
        startActivity(MainActivity.getIntent(this))
    }
}
