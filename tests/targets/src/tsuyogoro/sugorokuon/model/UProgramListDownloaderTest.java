package tsuyogoro.sugorokuon.model;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.datatype.Program;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

public class UProgramListDownloaderTest extends InstrumentationTestCase {
	
	private ProgramListDownloader mTarget;

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
	}

	@Override
	protected void tearDown() throws Exception {
		mTarget = null;
		super.tearDown();
	}
	
//	/**
//	 * 文化放送のデータを実際にDownloadしてみる。
//	 *  
//	 */
//	@LargeTest
//	public void testDownload_JOQR_01() {
//		mTarget = new ProgramListDownloader();
//		
//		String stationId = "QRR";
//		List<Program> result = mTarget.getWeeklyProgramList(
//				stationId, new DefaultHttpClient());
//
//		assertTrue(0 < result.size());
//		assertEquals(stationId, result.get(0).stationId);
//	}
//	
//	/**
//	 * BAYFMのデータを実際にDownloadしてみる。
//	 *  
//	 */
//	@LargeTest
//	public void testDownload_BAYFM_01() {
//		mTarget = new ProgramListDownloader();
//		
//		String stationId = "BAYFM78";
//		List<Program> result = mTarget.getWeeklyProgramList(
//				stationId, new DefaultHttpClient());
//
//		assertTrue(0 < result.size());
//		assertEquals(stationId, result.get(0).stationId);
//	}
	
}
