package tsuyogoro.sugorokuon.extension

import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse
import tsuyogoro.sugorokuon.station.Station

fun StationResponse.convertToStations() : List<Station> =
    stationList.map {
        Station(
            it.id,
            Area.values().first{ it.id == this.areaId },
            it.name,
            it.ascii_name,
            it.webSite,
            it.logos.map { l -> Station.Logo(l.width, l.height, l.url) },
            it.banner
        )
    }
