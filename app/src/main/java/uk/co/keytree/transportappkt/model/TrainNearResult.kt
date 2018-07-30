package uk.co.keytree.transportappkt.model

data class TrainNearResult(val minlon: Double, val minlat: Double, val maxLon: Double, val maxLat: Double,
                           val searchLon: Double, val searchLat: Double, val page: Int, val rpp: Int,
                           val total: Int, val request_time: String, val stations: List<Station>)