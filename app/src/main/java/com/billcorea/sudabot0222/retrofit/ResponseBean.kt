package com.billcorea.sudabot0222.retrofit

data class ResponseBean(
    var id : String,
    var generations : ArrayList<Generation>,
    var usage : Usage,
)

data class Generation(
    var text : String,
    var tokens : Int,
)

data class Usage (
    var prompt_tokens : Int,
    var generated_tokens : Int,
    var total_tokens : Int,
)