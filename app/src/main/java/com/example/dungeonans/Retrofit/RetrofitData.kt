package com.example.dungeonans.DataClass

import com.google.gson.annotations.SerializedName

/** Response 값이 없을때 (Ex : Post) 사용**/
data class NoneData(
    var success: Boolean,
    var errmsg: String
)
/** 클릭한 특정 게시물 데이터 얻을때 사용**/
data class ClickedPostData(
    var success : Boolean,
    var errmsg : String,
    var posting: List<posting_format_res>,
    var own : Boolean
)

/** 일반 게시물 데이터 얻을때(모든 게시판 처음에 띄울때, 스크롤 해서 띄울때) 사용**/
data class PostData(
    var success : Boolean,
    var errmsg : String,
    var posting_list : ArrayList<posting_format_res>,
    var end_index : Int,
    var board_tag_list : List<TagTextData>
)

/** 댓글 데이터 얻을 때 사용**/
data class Comment(
    var success: Boolean,
    var errmsg: String,
    var comment_list : List<comment_format_res>
)

/** 프로필 데이터 얻을 때 사용**/
data class ProfileData(
    var success: Boolean,
    var errmsg: String,
    var profile_list : List<profile_format>
)

data class CommunityPostData3(
    var success : Boolean,
    var errmsg : String,
    var posting_list : ArrayList<posting_format_res>,
    var end_index : Int,
    var board_tag_list : List<TagTextData>
)

//조수민 실험
data class AskPostResponse(
    var success : Boolean,
    var errmsg : String
)

data class GetCommunityPostByTag(
    var success: Boolean,
    var errmsg: String,
    var posting_list: List<posting_format_res>,
    var end_index: Int
)

data class send_post_cnt(
    var start_index: Int,
    var post_cnt : Int
)

data class board_ask_format(
    var board_index: String,
    var title:String,
    var content: String,
    var board_tag: String,
    var language_tag: language_tag
)
data class board_req_format(
    var start_index  : Int,
    var post_cnt : Int,
)

data class profile_format(
    var name : String,
    var nickname: String,
    var score: Int,
    var dev_level : String,
    var facebook_link : String,
    var github_link : String,
    var insta_link : String,
    var profile_photo : String
)
// dev_stack 추가 필요

data class posting_format_res(
    var board_index : Int,
    var posting_index : Int,
    var name : String,
    var id : String,
    var nickname : String,
    var title : String,
    var content : String,
    var date : String,
    var like_num : Int,
    var comment_num : Int,
    var board_tag : Int,
    var row_number : String
)

data class profile_format_res(
    var profile_index : Int,
    var id : String,
    var name : String,
    var nickname : String,
    var score : Int,
    var dev_stack : DevStack
)

data class DevStack(
    var cpp : Boolean,
    var sql : Boolean,
    var html : Boolean,
    var java: Boolean,
    var clang : Boolean,
    var shell : Boolean,
    var swift : Boolean,
    var kotlin: Boolean,
    var python: Boolean,
    var javascript: Boolean
)
data class TagTextData(
    var tagText : String
)

data class language_tag(
    var python : Boolean,
    var kotlin : Boolean,
    var javascript : Boolean,
    var html : Boolean,
    var cpp : Boolean,
    var clang : Boolean,
    var java : Boolean,
    var swift : Boolean,
    var sql : Boolean,
    var shell : Boolean
)
// profile_photo
// github_link
// insta_link

data class comment_format_req(
    var board_index: Int,
    var posting_index: Int,
    var content : String,
    var nested_comment_index : Int
)

data class comment_format_res(
    var board_index: Int,
    var posting_index: Int,
    var comment_index:Int,
    var name : String,
    var nickname: String,
    var content: String,
    var like_num: Int,
    var nested_comment_index: Int // 대댓글 갯수
)

data class put_comment_req(
    var posting_index: String,
    var content : String
)

data class LoginData(
    val id: String,
    val pw : String
)
/** 계정생성 Request **/
data class RegisterData(
    val id : String,
    val pw : String,
    val name : String,
    val nickname : String,
    val email : String
)

data class LoginResponse(
    val errmsg: String,
    val success : Boolean,
    val token : String
)

data class QueryData(
    @SerializedName("word") var query : String
)

