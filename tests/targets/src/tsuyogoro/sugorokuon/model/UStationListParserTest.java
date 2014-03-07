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
	 * 13�Ǖ��̃f�[�^(test_station_list_01.txt)
	 * ��ǂݍ���Ő������ǂݍ��܂�邱�Ƃ��m�F�B
	 * 
	 */
	@LargeTest
	public void testParse_StandardStationList_01() {
		// Test data��ǂݍ����inputStream�����
		Context c = getInstrumentation().getContext();
		Resources r = c.getResources();
		InputStream source = r.openRawResource(R.raw.test_station_list_01);

		mTarget = new StationListParser(source, LogoSize.XSMALL, "UTF-8");
		
		// Parser��test data��ǂݍ��܂���
		List<Station> result = mTarget.parse();
		
		// Null�͕Ԃ��Ă��Ȃ��͂��B
		assertNotNull(result);
		
		// ���ʂ��m�F�Btest_station_list�ɂ�13�̋Ǐ�񂪓����Ă���B
		assertEquals(13, result.size());
	
		// �eProgram��parse���ʂ̊m�F
		Station station01 = result.get(0);
		
		assertEquals("TBS", station01.id);
		assertEquals("TBSRadio", station01.name);
		assertEquals("TBS RADIO", station01.ascii_name);
		assertEquals("http://www.tbs.co.jp/radio/", station01.siteUrl);
		assertEquals("http://radiko.jp/station/logo/TBS/logo_xsmall.png", station01.logoUrl);
		assertEquals("http://radiko.jp/res/banner/TBS/20120329190305.png", station01.bannerUrl);
	}
	
}
