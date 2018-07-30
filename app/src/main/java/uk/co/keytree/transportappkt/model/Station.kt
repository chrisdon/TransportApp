package uk.co.keytree.transportappkt.model

data class Station(val station_code: String, val atcocode: String, val tiploc_code: String,
                   val name: String, val mode: String, val longitude: Double, val latitude: Double,
                   val distance: Int)