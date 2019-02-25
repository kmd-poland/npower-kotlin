package pl.kmdpoland.npower.data

import java.util.*

data class Visit(
    var firstName: String,
    var lastName: String,
    var startTime: Date,
    var avatar: String,
    var address: String,
    var coordinates: Array<Double>){

    var fullName = "$firstName $lastName"
}