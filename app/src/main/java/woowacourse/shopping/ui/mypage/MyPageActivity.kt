package woowacourse.shopping.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.members.MemberDto
import woowacourse.shopping.data.members.MemberRemoteService
import woowacourse.shopping.databinding.ActivityMyPageBinding
import woowacourse.shopping.ui.orders.OrdersActivity
import woowacourse.shopping.utils.UserData

class MyPageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMyPageBinding.inflate(layoutInflater)
    }

    private val memberRemoteService = MemberRemoteService.getInstance()

    private var spinnerInitializationFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initUserInfo()
        initMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarMyPage)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbarMyPage.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbarMyPage.navigationIcon = navigationIcon
    }

    private fun initUserInfo() {
        memberRemoteService.requestMembers().enqueue(object : retrofit2.Callback<List<MemberDto>> {
            override fun onResponse(
                call: Call<List<MemberDto>>,
                response: Response<List<MemberDto>>
            ) {
                if (response.isSuccessful.not()) return
                val members = response.body() ?: return
                binding.spinnerUserEmails.adapter = ArrayAdapter(
                    this@MyPageActivity,
                    android.R.layout.simple_spinner_item,
                    members.map(MemberDto::email).sorted()
                )
                binding.spinnerUserEmails.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (spinnerInitializationFlag.not()) {
                                spinnerInitializationFlag = true
                                return
                            }
                            val selectedEmail = binding.spinnerUserEmails.selectedItem
                            val loginMember = members.find { it.email == selectedEmail }
                                ?: throw IllegalStateException("서버랑 앱의 유저 정보가 맞지 않음. 서버랑 다시 상의하세요.")
                            UserData.credential = Base64.encodeToString(
                                "${loginMember.email}:${loginMember.password}".toByteArray(),
                                Base64.NO_WRAP
                            )
                            UserData.email = loginMember.email
                            UserData.grade = loginMember.grade
                            val preferences = getSharedPreferences("shopping", MODE_PRIVATE)
                            preferences.edit()
                                .putString("EMAIL", loginMember.email)
                                .putString("PASSWORD", loginMember.password)
                                .putString("GRADE", loginMember.grade)
                                .apply()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                    }
                val preferences = getSharedPreferences("shopping", MODE_PRIVATE)
                val loginEmail = preferences.getString("EMAIL", null)
                    ?: throw IllegalStateException("서버 선택 액티비티에서 필히 초기화했어야 합니다.")
                binding.spinnerUserEmails.setSelection(
                    when (loginEmail) {
                        "a@a.com" -> 0
                        "b@b.com" -> 1
                        "c@c.com" -> 2
                        else -> throw IllegalStateException("서버랑 앱의 유저 정보가 맞지 않음. 서버랑 다시 상의하세요.")
                    }
                )
            }

            override fun onFailure(call: Call<List<MemberDto>>, t: Throwable) {
            }
        })
    }

    private fun initMenu() {
        binding.tvOrderList.setOnClickListener {
            OrdersActivity.startActivity(this)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPageActivity::class.java)
            context.startActivity(intent)
        }
    }
}
