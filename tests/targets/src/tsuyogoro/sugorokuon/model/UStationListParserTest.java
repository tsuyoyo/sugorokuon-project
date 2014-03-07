package tsuyogoro.sugorokuon.model;

import java.io.InputStream;
import java.util.List;

import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.model.StationListParser.LogoSize;
import tsuyogoro.sugorokuon.test.R;
import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

public class UStationListParserTest extends InstrumentationTestCase {
	
	private StationListParser mTarget;

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
	 * 13局分のデータ(test_station_list_01.txt)
	 * を読み込んで正しく読み込まれることを確認。
	 * 
	 */
	@LargeTest
	public void testParse_StandardStationList_01() {
		// Test dataを読み込んでinputStreamを作る
		Context c = getInstrumentation().getContext();
		Resources r = c.getResources();
		InputStream source = r.openRawResource(R.raw.test_station_list_01);

		mTarget = new StationListParser(source, LogoSize.XSMALL, "UTF-8");
		
		// Parserにtest dataを読み込ませる
		List<Station> result = mTarget.parse();
		
		// Nullは返ってこないはず。
		assertNotNull(result);
		
		// 結果を確認。test_station_listには13個の局情報が入っている。
		assertEquals(13, result.size());
	
		// 各Programのparse結果の確認
		Station station01 = result.get(0);
		
		assertEquals("TBS", station01.id);
		assertEquals("TBSRadio", station01.name);
		assertEquals("TBS RADIO", station01.ascii_name);
		assertEquals("http://www.tbs.co.jp/radio/", station01.siteUrl);
		assertEquals("http://radiko.jp/station/logo/TBS/logo_xsmall.png", station01.logoUrl);
		assertEquals("http://radiko.jp/res/banner/TBS/20120329190305.png", station01.bannerUrl);
	}
	
}
