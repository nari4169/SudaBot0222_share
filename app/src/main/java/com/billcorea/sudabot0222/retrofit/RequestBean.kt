package com.billcorea.sudabot0222.retrofit

data class RequestBean(
    var prompt : String = "",
    var max_tokens : Int = 128,
    var temperature : Double = 0.2,
    var top_p : Double = 0.85,
    var n : Int = 1,
)
