package uk.co.keytree.transportappkt.model

data class PlacesResponse(val request_time: String, val source: String,
                          val acknowledgements: String, val member: List<Member>)