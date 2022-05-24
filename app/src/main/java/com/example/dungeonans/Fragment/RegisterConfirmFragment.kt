package com.example.dungeonans.Fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dungeonans.Activity.RegisterActivity
import com.example.dungeonans.DataClass.LoginResponse
import com.example.dungeonans.DataClass.NoneData
import com.example.dungeonans.DataClass.RegisterData
import com.example.dungeonans.R
import com.example.dungeonans.Retrofit.RetrofitClient
import com.example.dungeonans.Utils.PreferenceUtil
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_register_confirm.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterConfirmFragment : Fragment() {

   private lateinit var registerActivity : RegisterActivity
   private lateinit var preferenceUtil : PreferenceUtil

    // state boolean
    private var isIdOverlapping = false // 아이디 중복 여부
    private var isNameAcceptable = false // 이름 예외처리 여부
    private var isIdAcceptable = false // 아이디 예외처리 여부
    private var isPwAcceptable = false // 비밀번호 예외처리 여부
    private var isRePwAcceptable = false // 비밀번호 재입력 예외처리 여부
    private var isEmailAcceptable = false // 이메일 예외처리 여부

    // EditText
    lateinit var nameET : EditText
    lateinit var idET : EditText
    lateinit var pwET : EditText
    lateinit var rePwET : EditText
    lateinit var emailET : EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerActivity = activity as RegisterActivity
        preferenceUtil = PreferenceUtil(registerActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_register_confirm, container, false)

        // Button
        val nextPageBtn = view.findViewById<Button>(R.id.nextPageBtn)
        val backPageBtn = view.findViewById<ImageButton>(R.id.backPageBtn)
        val overlapCheckBtn = view.findViewById<Button>(R.id.idOverlapCheckBtn)

        // EditText
        nameET = view.findViewById(R.id.nameET)
        idET = view.findViewById(R.id.idET)
        pwET = view.findViewById(R.id.pwET)
        rePwET = view.findViewById(R.id.rePwET)
        emailET = view.findViewById(R.id.emailET)
        val editTextList = arrayListOf(nameET, idET, pwET, rePwET, emailET)

        // domain Spinner
        val domainList = resources.getStringArray(R.array.domain_array)
        val domainSpinner = view.findViewById<Spinner>(R.id.domainSpinner)

        // 도메인 스피너 적용
        domainSpinner?.adapter = ArrayAdapter(registerActivity, android.R.layout.simple_spinner_item, domainList)
        domainSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var doamain = domainSpinner.selectedItem.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        // 예외처리
        for (i in 0 until editTextList.size) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
//                    when (i) {
//                        0 -> preferenceUtil.setString("registerName", "")
//                        1 -> preferenceUtil.setString("registerId", "")
//                        2 -> preferenceUtil.setString("registerPw", "")
//                        4 -> preferenceUtil.setString("registerEmail", "")
//                    }
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    when (i) {
                        0 -> nameExceptionHandling(nameET) // 이름 예외처리 함수 호출부
                        1 -> idExceptionHandling(idET) // 아이디 예외처리 함수 호출부
                        2 -> pwExceptionHandling(pwET) // 비밀번호 예외처리 함수 호출부
                        3 -> rePwExceptionHandling(pwET, rePwET) // 비밀번호 재입력 예외처리 함수 호출부
                        4 -> emailExceptionHandling(emailET) // 이메일 예외처리 함수 호출부
                    }
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit })
        }

        overlapCheckBtn.setOnClickListener {
            overlapCheckEvent()
        }

        // 다음 페이지 이동 (회원가입 완료)
        nextPageBtn.setOnClickListener {
            moveNextPageEvent()
        }

        // 이전 페이지로 이동
        backPageBtn.setOnClickListener {
            moveBackPageEvent()
        }

        return view
    }

    // 이름 예외처리
    private fun nameExceptionHandling(nameET: EditText) {
        val nameIL = view?.findViewById<TextInputLayout>(R.id.nameInputLayout)
        isNameAcceptable = false

        if(nameET.length() < 2) {
            nameIL?.error = "이름을 입력해주세요."
        } else {
            nameIL?.error = null
            nameET.backgroundTintList = ContextCompat.getColorStateList(registerActivity,R.color.blue)
            nameIL?.hintTextColor = ContextCompat.getColorStateList(registerActivity,R.color.blue)
            isNameAcceptable = true
        }
    }

    // 아이디 예외처리
    private fun idExceptionHandling(idET: EditText) {
        val idIL = view?.findViewById<TextInputLayout>(R.id.idInputLayout)
        val overlapCheckBtn = view?.findViewById<Button>(R.id.idOverlapCheckBtn)
        isIdAcceptable = false
        isIdOverlapping = false

        if (idET.length() < 4) {
            idIL?.error = "4자 이상 입력해주세요."
            overlapCheckBtn?.isEnabled = false
        }
        else if (!Pattern.matches("^[A-Za-z0-9]*$",idET.text)) {
            idIL?.error = "아이디 형식에 부합하지 않습니다."
            overlapCheckBtn?.isEnabled = false
        }
        else {
            idIL?.error = null
            idET.backgroundTintList = ContextCompat.getColorStateList(registerActivity, R.color.blue)
            idIL?.hintTextColor = ContextCompat.getColorStateList(registerActivity, R.color.blue)
            overlapCheckBtn?.isEnabled = true
            isIdAcceptable = true
        }
    }

    // 비밀번호 예외처리
    private fun pwExceptionHandling(pwET: EditText) {
        val pwIL = view?.findViewById<TextInputLayout>(R.id.pwInputLayout)
        isPwAcceptable = false

        if (pwET.length() < 8) {
            pwIL?.error = "8자 이상 입력해주세요."
        } else if (!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwET.text)) {
            pwIL?.error = "비밀번호 형식이 올바르지 않습니다."
        } else {
            pwIL?.error = null
            pwET.backgroundTintList = ContextCompat.getColorStateList(registerActivity, R.color.blue)
            pwIL?.hintTextColor = ContextCompat.getColorStateList(registerActivity, R.color.blue)
            isPwAcceptable = true
        }
    }

    // 비밀번호 재입력 예외처리
    private fun rePwExceptionHandling(pwET: EditText, rePwET: EditText) {
        val rePwIL = view?.findViewById<TextInputLayout>(R.id.rePwInputLayout)
        isRePwAcceptable = false

        Log.d("TAG", "pw : ${pwET.text} rePw : ${rePwET.text}")
        if (pwET.text.toString() != rePwET.text.toString()) {
            rePwIL?.error = "비밀번호가 일치하지 않습니다."
        } else {
            rePwIL?.error = null
            rePwET.backgroundTintList = ContextCompat.getColorStateList(registerActivity, R.color.blue)
            rePwIL?.hintTextColor = ContextCompat.getColorStateList(registerActivity, R.color.blue)
            isRePwAcceptable = true
        }
    }

    // 이메일 예외처리
    private fun emailExceptionHandling(emailET: EditText) {
        val emailIL = view?.findViewById<TextInputLayout>(R.id.emailInputLayout)
        isEmailAcceptable = false

        if (emailET.length() < 1) {
            emailIL?.error = "이메일을 입력해주세요."
        }  else {
            emailIL?.error = null
            emailET.backgroundTintList = ContextCompat.getColorStateList(registerActivity, R.color.grey)
            emailIL?.hintTextColor = ContextCompat.getColorStateList(registerActivity, R.color.grey)
            isEmailAcceptable = true
        }
    }

    private fun overlapCheckEvent() {
        AlertDialog.Builder(registerActivity)
            .setMessage("사용가능한 아이디입니다.")
            .setPositiveButton("확인", null)
            .show()

        isIdOverlapping = true
    }

    private fun moveBackPageEvent() {
        registerActivity.transFragEvent(0)
    }

    private fun moveNextPageEvent() {
        if (!isNameAcceptable) registerActivity.showToastEvent("이름을 확인해주세요.", true)
        else if (!isIdAcceptable) registerActivity.showToastEvent("아이디를 확인해주세요.", true)
        else if (!isIdOverlapping) registerActivity.showToastEvent("아이디가 중복 확인되지 않았습니다.", true)
        else if (!isPwAcceptable) registerActivity.showToastEvent("비밀번호를 확인해주세요.", true)
        else if (!isRePwAcceptable) registerActivity.showToastEvent("비밀번호가 서로 일치하지 않습니다.", true)
        else if (!isEmailAcceptable) registerActivity.showToastEvent("이메일을 확인해주세요.", true)
        else {
            preferenceUtil.deletePreference(arrayListOf("registerName","registerId","registerPw","registerEmail"))
            connectRegisterApi()
            registerActivity.transFragEvent(2)
        }
    }

    private fun connectRegisterApi() {
        val registerData = RegisterData(
            id = idET.text.toString(),
            pw = pwET.text.toString(),
            name = nameET.text.toString(),
            nickname = nickNameET.text.toString(),
            email = emailET.text.toString()
        )
        val retrofit = RetrofitClient.initClient()
        val requestRegisterApi = retrofit.create(RetrofitClient.LoginApi::class.java)
        requestRegisterApi.postAccount(registerData = registerData).enqueue(object : Callback<NoneData> {
            override fun onResponse(call: Call<NoneData>, response: Response<NoneData>) {
                Log.d("TAG", "register success : ${response.body()!!.success}")
            }

            override fun onFailure(call: Call<NoneData>, t: Throwable) {
            }
        })
    }



}