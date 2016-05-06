package tsuyogoro.sugorokuon.network;

import java.util.List;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Station;

public interface IStationFetcher {

    /**
     * areaIdのリストに入っているareaに属する全てのラジオ局情報をdownloadする。
     * stationIdに重複はないよう、listを作成する。
     *
     * @param areas
     * @param logoSize
     * @return　downloadに失敗したらnullが返る。
     */
    List<Station> fetch(Area[] areas, StationLogoSize logoSize, String logoCacheDir);

    /**
     * areaIdで特定されるAreaのstation listをdownloadする。
     *
     * @param areaId
     * @param logoSize
     * @return downloadに失敗した場合はnullが返る。
     */
    List<Station> fetch(String areaId, StationLogoSize logoSize, String logoCacheDir);

}
