package com.example.dungeonans.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dungeonans.Activity.PostActivity
import com.example.dungeonans.Adapter.AskRVAdapter
import com.example.dungeonans.DataClass.*
import com.example.dungeonans.R
import com.example.dungeonans.Retrofit.RetrofitClient
import com.example.dungeonans.Space.LinearSpacingItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AskShowAllPostFragment : Fragment() {
    //조수민 수정 - 파라미터를 저장할 공간 생성
    lateinit var parameter : String
    lateinit var recyclerView : RecyclerView
    //
    var dataCount = 6
    lateinit var setData: MutableList<AskData>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.askpage_viewall_fragment2,container,false)
        var postSetMode = requireArguments().getString("Value")
        recyclerView = view.findViewById(R.id.askAllPostPageRecyclerView)
        parameter = postSetMode.toString()
        renderUi(0)
        setSpinner(view)


        var swipe = view.findViewById<SwipeRefreshLayout>(R.id.askpage_swapeview)
        swipe.setOnRefreshListener {
            renderUi(0)
            swipe.isRefreshing = false
        }

        return view
    }

    //조수민 수정 : Api 적용
    private fun renderUi(start_index : Int) {
        var retrofit = RetrofitClient.initClient()
        var data = board_req_format(start_index,6)
        //조수민 수정 : 만약 parameter 가 0 즉 전체 질문 보기라면
        if (parameter == "0") {
            var sendData = retrofit.create(RetrofitClient.AskApi::class.java)
            sendData.getAskPost(data).enqueue(object : Callback<PostData> {
                override fun onFailure(call: Call<PostData>, t: Throwable) {
                    Toast.makeText(context, "서버 연결이 불안정합니다", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                    var postingList = response.body()!!.posting_list
                    // 조수민 수정 : 게시물이 6개 미만이면 오류가 뜨기 때문에 try 써야됨
                    try {
                        setData = setData(parameter,dataCount, postingList)
                    }
                    catch (e:IndexOutOfBoundsException){
                        setData = setData(parameter,postingList.count(), postingList)
                    } finally {

                        var adapter = AskRVAdapter()
                        adapter.setItemClickListener(object : AskRVAdapter.OnItemClickListener{
                            override fun onClick(v: View, position: Int) {
                                var (board_index, posting_index, name, id, nickname,
                                    title, content, date, like_num, comment_num,
                                    board_tag, row_number) = postingList[position]
                                val intent = Intent(context, PostActivity::class.java)
                                intent.putExtra("posting_index",posting_index.toString())
                                intent.putExtra("name",name)
                                intent.putExtra("nickname",nickname)
                                intent.putExtra("title",title)
                                intent.putExtra("date",date)
                                startActivity(intent)
                            }
                        })
                        adapter.listData = setData
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        var space = LinearSpacingItemDecoration(20)
                        recyclerView.addItemDecoration(space)
//                    }
//                    catch (e:IndexOutOfBoundsException){
//
//                    }

                    }

                }
            })
        } else if (parameter == "1") {
            var sendData = retrofit.create(RetrofitClient.AskApi::class.java)
            sendData.getUnAnsweredPost(data).enqueue(object : Callback<PostData> {
                override fun onFailure(call: Call<PostData>, t: Throwable) {
                    Toast.makeText(context, "서버 연결이 불안정합니다", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                    var postingList = response.body()!!.posting_list
                    // 조수민 수정 : 게시물이 6개 미만이면 오류가 뜨기 때문에 try 써야됨
                    try {
                        setData = setData(parameter, dataCount, postingList)
                    }
                    catch (e:IndexOutOfBoundsException){
                        setData = setData(parameter,postingList.count(), postingList)
                    } finally {
                        var adapter = AskRVAdapter()
                        adapter.setItemClickListener(object : AskRVAdapter.OnItemClickListener{
                            override fun onClick(v: View, position: Int) {
                                var (board_index, posting_index, name, id, nickname,
                                    title, content, date, like_num, comment_num,
                                    board_tag, row_number) = postingList[position]
                                val intent = Intent(context, PostActivity::class.java)
                                intent.putExtra("posting_index",posting_index.toString())
                                intent.putExtra("name",name)
                                intent.putExtra("nickname",nickname)
                                intent.putExtra("title",title)
                                intent.putExtra("date",date)
                                startActivity(intent)
                            }
                        })
                        adapter.listData = setData
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        var space = LinearSpacingItemDecoration(20)
                        recyclerView.addItemDecoration(space)
                    }

                }
            })
        }

        else if (parameter == "2") {
            var sendData = retrofit.create(RetrofitClient.AskApi::class.java)
            sendData.getAnsweredPost(data).enqueue(object : Callback<PostData> {
                override fun onFailure(call: Call<PostData>, t: Throwable) {
                    Toast.makeText(context, "서버 연결이 불안정합니다", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                    var postingList = response.body()!!.posting_list
                    // 조수민 수정 : 게시물이 6개 미만이면 오류가 뜨기 때문에 try 써야됨ㅓ
                    try {
                        setData = setData(parameter, dataCount, postingList)
                    }
                    catch (e:IndexOutOfBoundsException){
                        setData = setData(parameter,postingList.count(), postingList)
                    } finally {
                        var adapter = AskRVAdapter()
                        adapter.setItemClickListener(object : AskRVAdapter.OnItemClickListener{
                            override fun onClick(v: View, position: Int) {
                                var (board_index, posting_index, name, id, nickname,
                                    title, content, date, like_num, comment_num,
                                    board_tag, row_number) = postingList[position]
                                val intent = Intent(context, PostActivity::class.java)
                                intent.putExtra("posting_index",posting_index.toString())
                                intent.putExtra("name",name)
                                intent.putExtra("nickname",nickname)
                                intent.putExtra("title",title)
                                intent.putExtra("date",date)
                                startActivity(intent)
                            }
                        })
                        adapter.listData = setData
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        var space = LinearSpacingItemDecoration(20)
                        recyclerView.addItemDecoration(space)
                    }
                }
            })
        }
    }

    private fun setSpinner(view:View) {
        val spinner = view.findViewById<Spinner>(R.id.setPostTypeSpinner)
        val spinnerItem = resources.getStringArray(R.array.setPostType)
        val myAdapter = object : ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,spinnerItem) {
            override fun getDropDownView(position: Int,convertView: View?,parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position,convertView,parent) as TextView
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP,10f)
                return view
            }
        }
        spinner.adapter = myAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View,position: Int,id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {
                    }
                    1 -> {
                    }
                    else -> {
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    //조수민 수정 api 에서 들어온 데이터에 맞춰서 작성. 이미지는 어떻게 할 건지 고민해봐야할듯?
    private fun setData(parameter : String, postCount: Int, postingData : List<posting_format_res>) : MutableList<AskData> {
        var data : MutableList<AskData>  = mutableListOf()
        for (index in 0 until postCount) {
            var askUserImage = index
            var askUserName = postingData[index].name
            var userNickName = postingData[index].nickname
            var postTitle = postingData[index].title
            var postBody = postingData[index].content.replace("<p></p>", " ")
            var askStatusImage = index
            if (parameter == "1") {
                askStatusImage = R.drawable.unanswered_icon
            } else if (parameter == "2") {
                askStatusImage = R.drawable.answered_icon
            } else {
                askStatusImage = R.drawable.answered_icon
            }
            var likeCount = postingData[index].like_num.toString()
            var commentCount = postingData[index].comment_num.toString()
            var listData = AskData(askUserImage,askUserName,userNickName,postTitle,postBody,askStatusImage,likeCount,commentCount)
            data.add(listData)
        }
        return data
    }
}