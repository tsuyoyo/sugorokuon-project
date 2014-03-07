package tsuyogoro.sugorokuon.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.model.StationListParser.LogoSize;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

/**
 * 
 * <h1 id="エリアコード対照表">エリアコード対照表</h1>
 * <table class="wiki">
 * <tr><td>area_id</td><td>area_name
 * </td></tr><tr><td>JP1</td><td>HOKKAIDO JAPAN
 * </td></tr><tr><td>JP2</td><td>AOMORI JAPAN
 * </td></tr><tr><td>JP3</td><td>IWATE JAPAN
 * </td></tr><tr><td>JP4</td><td>MIYAGI JAPAN
 * </td></tr><tr><td>JP5</td><td>AKITA JAPAN
 * </td></tr><tr><td>JP6</td><td>YAMAGATA JAPAN
 * </td></tr><tr><td>JP7</td><td>FUKUSHIMA JAPAN
 * </td></tr><tr><td>JP8</td><td>IBARAKI JAPAN
 * </td></tr><tr><td>JP9</td><td>TOCHIGI JAPAN
 * </td></tr><tr><td>JP10</td><td>GUNMA JAPAN
 * </td></tr><tr><td>JP11</td><td>SAITAMA JAPAN
 * </td></tr><tr><td>JP12</td><td>CHIBA JAPAN
 * </td></tr><tr><td>JP13</td><td>TOKYO JAPAN
 * </td></tr><tr><td>JP14</td><td>KANAGAWA JAPAN
 * </td></tr><tr><td>JP15</td><td>NIIGATA JAPAN
 * </td></tr><tr><td>JP16</td><td>TOYAMA JAPAN
 * </td></tr><tr><td>JP17</td><td>ISHIKAWA JAPAN
 * </td></tr><tr><td>JP18</td><td>FUKUI JAPAN
 * </td></tr><tr><td>JP19</td><td>YAMANASHI JAPAN
 * </td></tr><tr><td>JP20</td><td>NAGANO JAPAN
 * </td></tr><tr><td>JP21</td><td>GIFU JAPAN
 * </td></tr><tr><td>JP22</td><td>SHIZUOKA JAPAN
 * </td></tr><tr><td>JP23</td><td>AICHI JAPAN
 * </td></tr><tr><td>JP24</td><td>MIE JAPAN
 * </td></tr><tr><td>JP25</td><td>SHIGA JAPAN
 * </td></tr><tr><td>JP26</td><td>KYOTO JAPAN
 * </td></tr><tr><td>JP27</td><td>OSAKA JAPAN
 * </td></tr><tr><td>JP28</td><td>HYOGO JAPAN
 * </td></tr><tr><td>JP29</td><td>NARA JAPAN
 * </td></tr><tr><td>JP30</td><td>WAKAYAMA JAPAN
 * </td></tr><tr><td>JP31</td><td>TOTTORI JAPAN
 * </td></tr><tr><td>JP32</td><td>SHIMANE JAPAN
 * </td></tr><tr><td>JP33</td><td>OKAYAMA JAPAN
 * </td></tr><tr><td>JP34</td><td>HIROSHIMA JAPAN
 * </td></tr><tr><td>JP35</td><td>YAMAGUCHI JAPAN
 * </td></tr><tr><td>JP36</td><td>TOKUSHIMA JAPAN
 * </td></tr><tr><td>JP37</td><td>KAGAWA JAPAN
 * </td></tr><tr><td>JP38</td><td>EHIME JAPAN
 * </td></tr><tr><td>JP39</td><td>KOUCHI JAPAN
 * </td></tr><tr><td>JP40</td><td>FUKUOKA JAPAN
 * </td></tr><tr><td>JP41</td><td>SAGA JAPAN
 * </td></tr><tr><td>JP42</td><td>NAGASAKI JAPAN
 * </td></tr><tr><td>JP43</td><td>KUMAMOTO JAPAN
 * </td></tr><tr><td>JP44</td><td>OHITA JAPAN
 * </td></tr><tr><td>JP45</td><td>MIYAZAKI JAPAN
 * </td></tr><tr><td>JP46</td><td>KAGOSHIMA JAPAN
 * </td></tr><tr><td>JP47</td><td>OKINAWA JAPAN
 * </td></tr></table>
 * 
 * @author Tsuyoyo
 *
 */
public class UStationListDownloaderTest extends InstrumentationTestCase {
	
	private StationListDownloader mTarget;

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
	}

	@Override
	protected void tearDown() throws Exception {
		mTarget = null;
		super.tearDown();
	}
	
	/**
	 * 東京(TOKYO JAPAN)のstation listを実際にDownloadしてみる。
	 *  
	 */
	@LargeTest
	public void testDownload_TOKYO_JAPAN_01() {
		mTarget = new StationListDownloader();
		
		String areaId = "JP13";
		List<Station> result = mTarget.getStationList(
				areaId, LogoSize.XSMALL, new DefaultHttpClient());

		assertTrue(0 < result.size());
	}
	
//	/**
//	 * 東京近郊のstation listを実際にDownloadしてみて、
//	 * 重複無しでデータが取れているか確認。
//	 * 
//	 */
//	@LargeTest
//	public void testDownload_AroundTokyoStation_01() {
//		mTarget = new StationListDownloader();
//				
//		String saitamaAreaId  = "JP11";
//		String chibaAreaId    = "JP12";
//		String tokyoAreaId    = "JP13";
//		String kanagawaAreaId = "JP14";
//		
//		List<Station> tokyoResult = mTarget.getStationList(
//				tokyoAreaId, LogoSize.XSMALL, new DefaultHttpClient());
//		
//		List<String> areaList = new ArrayList<String>();
//		areaList.add(tokyoAreaId);
//		areaList.add(chibaAreaId);
//		areaList.add(saitamaAreaId);
//		areaList.add(kanagawaAreaId);
//		
//		List<Station> tokyoAroundResult = mTarget.getStationList(
//				areaList, LogoSize.XSMALL, new DefaultHttpClient());
//
//		assertEquals(tokyoAroundResult.size(), tokyoResult.size());
//	}
//	
//	/**
//	 * 関東全域のstation listを実際にDownloadしてみる。
//	 * 
//	 */
//	@LargeTest
//	public void testDownload_KantoStation_01() {
//		mTarget = new StationListDownloader();
//		
//		String ibarakiAreaId = "JP8";
//		String tochigiAreaId = "JP9";
//		String gunmaAreaId    = "JP10";
//		String saitamaAreaId  = "JP11";
//		String chibaAreaId    = "JP12";
//		String tokyoAreaId    = "JP13";
//		String kanagawaAreaId = "JP14";
//
//		
//		List<Station> tokyoResult = mTarget.getStationList(
//				tokyoAreaId, LogoSize.XSMALL, new DefaultHttpClient());
//		
//		List<String> areaList = new ArrayList<String>();
//		areaList.add(tokyoAreaId);
//		areaList.add(chibaAreaId);
//		areaList.add(saitamaAreaId);
//		areaList.add(kanagawaAreaId);
//		areaList.add(gunmaAreaId);
//		areaList.add(tochigiAreaId);
//		areaList.add(ibarakiAreaId);
//		
//		List<Station> kantoResult = mTarget.getStationList(
//				areaList, LogoSize.XSMALL, new DefaultHttpClient());
//
//		assertTrue(kantoResult.size() > tokyoResult.size());
//	}
}
