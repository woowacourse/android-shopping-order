package woowacourse.shopping.view.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityMyPageBinding
import woowacourse.shopping.model.data.repository.MemberRepositoryImpl
import woowacourse.shopping.server.retrofit.RetrofitClient
import woowacourse.shopping.view.orderhistory.OrderHistoryActivity

class MyPageActivity : AppCompatActivity(), MyPageContract.View {

    private lateinit var binding: ActivityMyPageBinding
    override lateinit var presenter: MyPageContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter()
        setPointView()
        setOnButtonClick()
    }

    private fun setPresenter() {
        presenter = MyPagePresenter(this, MemberRepositoryImpl(RetrofitClient.membersService))
    }

    override fun updatePointView(point: Int) {
        binding.tvPoint.text = POINT_FORMAT.format(point)
    }

    private fun setPointView() {
        presenter.getMemberInfo()
    }

    private fun setOnButtonClick() {
        binding.tvOrderHistoryButton.setOnClickListener {
            val intent = OrderHistoryActivity.intent(this)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val POINT_FORMAT = "%,d원"

        fun intent(context: Context): Intent {
            return Intent(context, MyPageActivity::class.java)
        }
    }
}
