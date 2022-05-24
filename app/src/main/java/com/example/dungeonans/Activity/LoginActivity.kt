package com.example.dungeonans.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dungeonans.DataClass.LoginData
import com.example.dungeonans.DataClass.LoginResponse
import com.example.dungeonans.R
import com.example.dungeonans.Retrofit.RetrofitClient
import com.example.dungeonans.Utils.Constants.TAG
import com.example.dungeonans.Utils.PrefManager
import com.example.dungeonans.databinding.ActivityLoginBinding
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Dungeonans_Material)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)

        binding.ibNaverLogin.setOnClickListener {
            startNaverLogin()
        }

        binding.btnFindAccount.setOnClickListener {
            findExistingAccountEvent()
        }

        binding.btnRegister.setOnClickListener {
            registerEvent()
        }

        binding.loginBtn.setOnClickListener {
            loginEvent()
        }
    }

    // 네이버 소셜 로그인
    private fun startNaverLogin(){
        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                val userId = result.profile?.id
                val userName = result.profile?.name

            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@LoginActivity, "errorCode: ${errorCode}\n" +
                        "errorDescription: $errorDescription", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공
                naverToken = NaverIdLoginSDK.getAccessToken()
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@LoginActivity, "errorCode: ${errorCode}\n" +
                        "errorDescription: $errorDescription", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    private fun loginEvent() {
        connectLoginApi()
//        val loginIntent = Intent(this, MainActivity::class.java)
//        startActivity(loginIntent)
    }

    private fun findExistingAccountEvent() {
        AlertDialog.Builder(this)
            .setView(R.layout.findaccount_dialog_layout)
            .setCancelable(true)
            .show()
            .also { alertDialog ->

                if(alertDialog == null) {
                    return@also
                }

                val findIdBtn = alertDialog.findViewById<Button>(R.id.findIdBtn)
                val findPwBtn = alertDialog.findViewById<Button>(R.id.findPwBtn)

                findIdBtn?.setOnClickListener{ // id 찾기
                    alertDialog.dismiss()
                    val intent = Intent(this, FindAccountActivity::class.java) // 계정 찾기 액티비티 전환
                    intent.putExtra("find","id")
                    startActivity(intent)
                }

                findPwBtn?.setOnClickListener {  // pw 찾기
                    alertDialog.dismiss()
                    val intent = Intent(this, FindAccountActivity::class.java) // 계정 찾기 액티비티 전환
                    intent.putExtra("find","pw")
                    startActivity(intent) // findPwFrag 전환
                }
            }
    }

    private fun registerEvent() {
        val registerIntent = Intent(this, RegisterActivity::class.java) // 회원가입 페이지로 전환
        startActivity(registerIntent)
    }

    private fun connectLoginApi() {
        val idValue = binding.etId.text.toString()
        val pwValue = binding.etPw.text.toString()

        val loginInfo = LoginData(
            id = idValue,
            pw = pwValue
        )

        val retrofit = RetrofitClient.initClient()
        val requestLoginApi = retrofit.create(RetrofitClient.LoginApi::class.java)
        requestLoginApi.postLogin(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.body()?.success == true) {
                    val loginIntent = Intent(this@LoginActivity, MainActivity::class.java) // 메인 페이지로 전환
                    if (PrefManager.getUserToken().isNotEmpty()) {
                        PrefManager.deleteUserToken()
                    }
                    PrefManager.storeUserToken(response.body()!!.token)
                    startActivity(loginIntent)
                    }
                    else {
                        Log.d("TAG","errmsg : ${response.body()!!.errmsg}")
                        Toast.makeText(this@LoginActivity, "잘못된 아이디 혹은 비밀번호 입니다.",Toast.LENGTH_SHORT).show()
                    }
            }
        })
    }

    // 네이버 로그아웃
    private fun startNaverLogout() {
        NaverIdLoginSDK.logout()
        Toast.makeText(this@LoginActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    // 네이버 연동 해제
    private fun startNaverDeleteToken(){
        NidOAuthLogin().callDeleteTokenApi(this, object : OAuthLoginCallback {
            override fun onSuccess() {
                Log.d(TAG,"네이버 토근 삭제")
            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업 X
                Log.d("naver", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d("naver", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }
}
