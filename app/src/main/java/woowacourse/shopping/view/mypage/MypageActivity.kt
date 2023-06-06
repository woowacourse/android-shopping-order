package woowacourse.shopping.view.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.impl.MypageRemoteRepository
import woowacourse.shopping.data.repository.impl.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityMypageBinding

class MypageActivity : AppCompatActivity(), MypageContract.View {
    private val binding: ActivityMypageBinding by lazy {
        ActivityMypageBinding.inflate(
            layoutInflater,
        )
    }
    private val presenter: MypageContract.Presenter by lazy {
        MypagePresenter(
            this,
            MypageRemoteRepository(ServerPreferencesRepository(this)),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpBinding()
        setUpActionBar()
        presenter.fetchCash()
    }

    private fun setUpBinding() {
        binding.presenter = presenter
        binding.lifecycleOwner = this
        binding.btnSubmit.setOnClickListener {
            presenter.chargeCash(Integer.parseInt(binding.editCash.text.toString()))
        }
    }

    override fun showNotSuccessfulErrorToast() {
        Toast.makeText(this, getString(R.string.server_communication_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerFailureToast() {
        Toast.makeText(this, getString(R.string.server_not_response_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerResponseWrongToast() {
        Toast.makeText(this, getString(R.string.server_response_wrong), Toast.LENGTH_LONG).show()
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showNegativeIntErrorToast() {
        Toast.makeText(this, getString(R.string.unable_use_negative_int), Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MypageActivity::class.java)
        }
    }
}
