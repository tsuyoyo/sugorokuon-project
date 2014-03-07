package tsuyogoro.sugorokuon.viewflow;

import android.test.InstrumentationTestCase;

public class UStationInfoManagerTest extends InstrumentationTestCase {

	private StationInfoViewFlow mTarget;

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
	 * Stationデータを更新してみる。
	 * 更新が上手くいっている事と、Logoのデータがちゃんとダウンロードされていることをcheck。
	 * 
	 */
//	@LargeTest
//	public void test_UpdateStationData_01() {
//		mTarget = StationInfoViewFlow.getInstance(getInstrumentation().getContext());
//		assertNotNull(mTarget);
//		
//		// 更新してみる
//		Area[] targetAreaArray = new Area[] { Area.TOKYO, Area.CHIBA, Area.TOCHIGI, Area.SAITAMA, Area.KANAGAWA, Area.GUNMA };
//		mTarget.updateStationData(targetAreaArray, LogoSize.XSMALL, new DefaultHttpClient());
//
//		// ちゃんとstationInfoが更新されているか？
//		List<Station> stationInfo = mTarget.getStationInfo();
//		assertTrue(0 < stationInfo.size());
//
//		// 全てのstationに対して、ちゃんとlogoがdownloadされたことをチェック。
//		for(Station s : stationInfo) {
//			Bitmap b = mTarget.getLogo(s.id);
//			assertNotNull(b);
//			
//			// XSMALLは105 x 33くらい
//			assertTrue( (30 < b.getHeight()) && (40 > b.getHeight()));
//			assertTrue( (100 < b.getWidth()) && (110 > b.getWidth()));
//		}
//	}
	
}
