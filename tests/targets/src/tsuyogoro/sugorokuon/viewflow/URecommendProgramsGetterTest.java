package tsuyogoro.sugorokuon.viewflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.model.StationListParser.LogoSize;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

public class URecommendProgramsGetterTest extends InstrumentationTestCase {
	
//	private RecommedProgramsGetter mTarget;

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
	}

	@Override
	protected void tearDown() throws Exception {
//		mTarget = null;
		super.tearDown();
	}
	
	/**
	 * AKB48Ç≈åüçıÇµÇƒÇ›ÇÈÅB
	 * 
	 */
	@LargeTest
	public void test_CreateRecommend_For_AKBota_01() {
//		mTarget = new RecommedProgramsGetter(getInstrumentation().getContext());
//		StationInfoViewFlow.getInstance(getInstrumentation().getContext()).updateStationData(
//				LogoSize.XSMALL, new DefaultHttpClient());
//		
//		List<String> keywords = new ArrayList<String>();
//		keywords.add("AKB48");
//		
//		List<Program> recommends = mTarget.getRecommendPrograms(keywords);
//		
//		assertTrue(0 < recommends.size());
	}
	
}
