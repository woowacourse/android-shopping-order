package woowacourse.shopping.view.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityMyPageBinding
import woowacourse.shopping.server.retrofit.RetrofitClient
import woowacourse.shopping.server.retrofit.createResponseCallback
import woowacourse.shopping.view.orderhistory.OrderHistoryActivity

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPointView()
        setOnButtonClick()
    }

    private fun setPointView() {
        RetrofitClient.membersService.getPoint().enqueue(
            createResponseCallback(
                onSuccess = { member ->
                    binding.tvPoint.text = POINT_FORMAT.format(member.point)
                },
                onFailure = {
                    throw IllegalStateException(NOT_FOUNT_POINT_ERROR)
                }
            )
        )
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
        private const val NOT_FOUNT_POINT_ERROR = "포인트를 불러오는데 실패하였습니다."

        fun intent(context: Context): Intent {
            return Intent(context, MyPageActivity::class.java)
        }
    }
}
