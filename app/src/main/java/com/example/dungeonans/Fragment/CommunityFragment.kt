package com.example.dungeonans.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dungeonans.Activity.MainActivity
import com.example.dungeonans.Adapter.CommunityRVAdapter
import com.example.dungeonans.DataClass.*
import com.example.dungeonans.R
import com.example.dungeonans.Retrofit.RetrofitClient
import com.example.dungeonans.Space.LinearSpacingItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CommunityFragment : Fragment() {
    //조수민 수정 : boarding_index 가 1인 posting_format_res 를 담는 리스트
    var communityPostingList = ArrayList<posting_format_res>()
    //조수민 수정 : boarding_index 가 1인 ... api 가 달라서 따로 배열을 만들어야 할듯
    var communityHotPostList = ArrayList<posting_format_res>()
    // 한번 스크롤 내릴때마다 + 6 씩
    var my_start_index = 0
    //
    var selectedBtn : Int? = null
    var postData : MutableList<CommunityData>  = mutableListOf()
    lateinit var sendData : MutableList<CommunityData>
    var start_index = 0
    var end_index = 0

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.communitypage_fragment2,container,false)
        setHashTag(view)
        renderPost(view,my_start_index)
        renderHotPost(view)

        var swipeCount = 0
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swapeView)
        swipe.setOnRefreshListener {
            swipeCount += 1
            val retrofit = RetrofitClient.initClient()
            val getCommunityHotPostApi = retrofit.create(RetrofitClient.CommunityApi::class.java)
//            val data = send_post_cnt(swipeCount,2)
            getCommunityHotPostApi.getCommunityHotPost(0,2).enqueue(object : Callback<PostData> {
                override fun onFailure(call: Call<PostData>, t: Throwable) {
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                    val recyclerView : RecyclerView = view.findViewById(R.id.communityPageHotPostRecyclerView)
                    sendData = try {
                        setHotPostData(2, response.body()!!.posting_list)
                    } catch (e: IndexOutOfBoundsException) {
                        setHotPostData(response.body()!!.posting_list.count(),response.body()!!.posting_list)
                    } finally {
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
            })
            swipe.isRefreshing = false
        }
        return view
    }

    private fun setHashTag(view:View) {
        var radioGroup : RadioGroup = view.findViewById(R.id.radioGroup)
        var radioButtonText = resources.getStringArray(R.array.hashtaglist)

        // 라디오 버튼 생성
        for (index in 0 until radioButtonText.count()) {
            var radioButton = layoutInflater.inflate(R.layout.hashtag_radiobutton,null)
            radioButton.id = index
            var buttonParams = RadioGroup.LayoutParams(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70f,resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30f,resources.displayMetrics).toInt())
            buttonParams.setMargins(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10f,resources.displayMetrics).toInt(),0,0,0)
            radioButton.layoutParams = buttonParams
            radioGroup.addView(radioButton)
        }

        // 라디오 버튼 텍스트 설정, 선택 해제 로직
        for (index in 0 until radioButtonText.count()) {
            var radioButton = view.findViewById<RadioButton>(index)
            radioButton.text = radioButtonText[index]
            radioButton.setOnClickListener{
                if (selectedBtn == index) {
                    radioButton.isChecked = false
                    view.findViewById<RadioButton>(selectedBtn!!).setTextColor(resources.getColor(R.color.black,null))
                }
                selectedBtn = index
            }
        }
        // 라디오 버튼 선택 해제 로직
        radioGroup.setOnCheckedChangeListener{ _, checkedId ->
            if (selectedBtn != null) {
                view.findViewById<RadioButton>(selectedBtn!!).setTextColor(resources.getColor(R.color.black,null))
            }
            when(checkedId) {
                checkedId ->  {
                    var checkedBtn = view.findViewById<RadioButton>(checkedId)
                    checkedBtn.setTextColor(resources.getColor(R.color.white,null))
                }
            }
        }
    }


    private fun renderPost(view: View, start_index : Int) {
        var mainLayout: LinearLayout = view.findViewById(R.id.mainLayout)
        var communityPageRecyclerView: RecyclerView = view.findViewById(R.id.communityPageRecyclerView)
        communityPageRecyclerView.visibility = View.GONE

        var retrofit = RetrofitClient.initClient()
        var getCommunityPostApi = retrofit.create(RetrofitClient.CommunityApi::class.java)
        getCommunityPostApi.getCommunityPost(start_index,6).enqueue(object : Callback<PostData> {
            override fun onFailure(call: Call<PostData>, t: Throwable) {
                Toast.makeText(context, "서버 연결이 불안정합니다", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                end_index = response.body()!!.end_index
                //조수민 수정 : 전체 posting_format_res 를 받고, for 문 돌려서 index 가 1인것 찾고, 저 위 선언해놓았던 배열에 넣어주기
                for (i in 0..response.body()!!.posting_list.size - 1) {
                    var (board_index, posting_index, name, id, nickname,
                        title, content, data, like_num, comment_num, board_tag, row_number) = response.body()!!.posting_list[i]
                    if (board_index == 1) {
                        communityPostingList.add(response.body()!!.posting_list[i])
                    }
                }
                //
                //조수민 수정 : setPostData 에 위에 배열 삽입
                sendData = setPostData(6, response.body()!!.posting_list)
                //
                var adapter = CommunityRVAdapter()
                adapter.setItemClickListener(object : CommunityRVAdapter.OnItemClickListener {
                    override fun postClick(v: View, position: Int) {
                        var mainActivity = context as MainActivity
                        Log.d("클릭됨!", this.toString())
                        var posting_list = response.body()!!.posting_list[position].posting_index
                        var posting = response.body()!!.posting_list[position].content
                        var name = response.body()!!.posting_list[position].name
                        var nickname = response.body()!!.posting_list[position].nickname
                        var title = response.body()!!.posting_list[position].title
                        var date = response.body()!!.posting_list[position].date
                        mainActivity.showPost(posting_list,posting,name,nickname,title,date)
                    }
                })
                adapter.communityList = sendData
                communityPageRecyclerView.adapter = adapter
                communityPageRecyclerView.layoutManager = LinearLayoutManager(context)
                var space = LinearSpacingItemDecoration(10)
                communityPageRecyclerView.addItemDecoration(space)
                mainLayout.visibility = View.VISIBLE
                communityPageRecyclerView.visibility = View.VISIBLE
                connectScrollListener(view)
                my_start_index += 6

            }
        })
    }

    private fun renderHotPost(view : View) {
        var mainLayout : LinearLayout = view.findViewById(R.id.mainLayout)
        var retrofit = RetrofitClient.initClient()
        var getCommunityHotPostApi = retrofit.create(RetrofitClient.CommunityApi::class.java)
//        var data = send_post_cnt(0,2)
        getCommunityHotPostApi.getCommunityHotPost(0,2).enqueue(object : Callback<PostData> {
            override fun onFailure(call: Call<PostData>, t: Throwable) {
                Toast.makeText(context,"서버 연결이 불안정합니다",Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<PostData>,response: Response<PostData>) {
                val recyclerView : RecyclerView = view.findViewById(R.id.communityPageHotPostRecyclerView)
                //조수민 수정 : communityHotPoistList 에 저장
                for (i in 0..response.body()!!.posting_list.size-1){
                    val (board_index, posting_index, name, id, nickname,
                        title, content,data,like_num,comment_num, board_tag,row_number) = response.body()!!.posting_list[i]
                    if (board_index == 1){
                        communityHotPostList.add(response.body()!!.posting_list[i])
                    }
                }

                //조수민 수정 : setPostData 에 위에 배열 삽입
                sendData = setHotPostData(2,communityHotPostList)
                //
                val adapter = CommunityRVAdapter()
                adapter.setItemClickListener(object : CommunityRVAdapter.OnItemClickListener {
                    override fun postClick(v: View, position: Int) {
                        var mainActivity = context as MainActivity
                        Log.d("클릭됨!", this.toString())
                        Log.d("position",position.toString())
                        var posting_index = response.body()!!.posting_list[position].posting_index
                        var posting = response.body()!!.posting_list[position].content
                        var name = response.body()!!.posting_list[position].name
                        var nickname = response.body()!!.posting_list[position].nickname
                        var title = response.body()!!.posting_list[position].title
                        var date = response.body()!!.posting_list[position].date
                        mainActivity.showPost(posting_index,posting,name,nickname,title,date)
                    }
                })
                adapter.communityList = sendData
                recyclerView.adapter = adapter
                LinearLayoutManager(context).also { recyclerView.layoutManager = it }
                val space = LinearSpacingItemDecoration(10)
                recyclerView.addItemDecoration(space)
                mainLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun setPostData(postCount : Int, postingData : ArrayList<posting_format_res>) : MutableList<CommunityData> {
        for (index in 0 until postCount) {
            val postTitle = postingData[index].title
            val postBody = postingData[index].content
            val hashtag = postingData[index].board_tag.toString()
            val likeCount = postingData[index].like_num.toString()
            val commentCount = postingData[index].comment_num.toString()
            val listData = CommunityData(postTitle,postBody,hashtag,likeCount,commentCount)
            postData.add(listData)
        }
        return postData
    }

    private fun setHotPostData(postCount : Int, postingData : ArrayList<posting_format_res>) : MutableList<CommunityData> {
        var data : MutableList<CommunityData> = mutableListOf()
        for (index in 0 until postCount) {
            val postTitle = postingData[index].title
            val postBody = postingData[index].content
            val hashtag = postingData[index].board_tag.toString()
            val likeCount = postingData[index].like_num.toString()
            val commentCount = postingData[index].comment_num.toString()
            val listData = CommunityData(postTitle,postBody,hashtag,likeCount,commentCount)
            data.add(listData)
        }
        return data
    }

    // start_index에 end_index를 넣어서 보내줘야 하는데, 문제는 12개 이후로 데이터가 들어오지 않는다..
    private fun connectScrollListener(view:View) {
        var scrollView = view.findViewById<NestedScrollView>(R.id.communityPageScrollView)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY: Int = scrollView.scrollY
            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollY)) {
                val retrofit = RetrofitClient.initClient()
                val getCommunityPostApi = retrofit.create(RetrofitClient.CommunityApi::class.java)
                getCommunityPostApi.getCommunityPost(end_index, 6).enqueue(
                    object : Callback<PostData> {
                        override fun onFailure(call: Call<PostData>, t: Throwable) {
                        }
                        override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                            end_index = response.body()!!.end_index
                            val communityPageRecyclerView = view.findViewById<RecyclerView>(R.id.communityPageRecyclerView)
                            val newPosition = communityPageRecyclerView.adapter!!.itemCount
                            sendData = try {
                                setPostData(6, response.body()!!.posting_list)
                            } catch (e: IndexOutOfBoundsException) {
                                setPostData(response.body()!!.posting_list.count(),response.body()!!.posting_list,)
                            } finally {
                                communityPageRecyclerView.adapter!!.notifyItemInserted(
                                    newPosition
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
