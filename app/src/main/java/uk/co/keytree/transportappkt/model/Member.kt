package uk.co.keytree.transportappkt.model

data class Member(val type: String, val station_code: String = "", val atcocode: String = "",
                  val tiploc_code: String = "", val description: String = "", val name: String,
                  val accuracy: Int, val longitude: Double, val latitude: Double, val distance: Int)