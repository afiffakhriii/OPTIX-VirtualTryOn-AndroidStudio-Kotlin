package com.afif.optix.data.entity

data class UserEntity(
    val name: String,
    val email: String,
    val imagePath: String = ""
){
    constructor(): this("", "","")
}
