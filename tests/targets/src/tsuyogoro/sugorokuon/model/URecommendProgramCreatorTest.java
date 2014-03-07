package tsuyogoro.sugorokuon.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.model.StationListParser.LogoSize;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

public class URecommendProgramCreatorTest extends InstrumentationTestCase {
	
//	private RecommendProgramCreator mTarget;
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();	
//	}
//
//	@Override
//	protected void tearDown() throws Exception {
//		mTarget = null;
//		super.tearDown();
//	}
//	
//	/**
//	 * Stringのparseのtest。
//	 * 
//	 */
//	@LargeTest
//	public void testParse_ParseStringTest_01() {
//		String target = "会いにいけるアイドル“ＡＫＢ４８”に、毎週金曜日のオールナイトニッポンでも会える！<br>けれども、毎回メンバーシャッフルでお送りするＡＫＢ４８のオールナイトニッポン！！";
//		String keyword = "AKB48";
//		String zenkaku = "ＡＫＢ４８";
//		assertFalse(target.matches(".*" + keyword + ".*"));
//		assertTrue(target.matches(".*" + zenkaku + ".*"));
//	}
//	
//	/**
//	 * AKB48オタにオススメの番組を取る(ニッポン放送から)。
//	 * 
//	 */
//	@LargeTest
//	public void testGetRecommendProgramsForAKBOta_01() {
//
//		// ニッポン放送から落とす
//		ProgramListDownloader progDownloader = new ProgramListDownloader();
//		List<Program> allPrograms = progDownloader.getWeeklyProgramList("LFR", new DefaultHttpClient());
//	
//		List<String> keyWords = new ArrayList<String>();
//		keyWords.add("AKB48");
//		
//		mTarget = new RecommendProgramCreator();
//		List<Program> recommends = mTarget.createRecommendProgram(allPrograms, keyWords);
//		
//		assertTrue(0 < recommends.size());
//		assertFalse(allPrograms.size() == recommends.size());	
//	}
//	
//	/**
//	 * AKB48オタにオススメの番組を取る(関東全域から)。
//	 * 
//	 */
//	@LargeTest
//	public void testGetRecommendProgramsForAKBOta_02() {
//
//		// 関東全域エリア
//		Area[] areas = new Area[]{ Area.TOKYO, Area.CHIBA, Area.KANAGAWA, Area.SAITAMA, Area.TOCHIGI, Area.GUNMA };
//		StationListDownloader stationListDownloader = new StationListDownloader();
//		
//		ProgramListDownloader progDownloader = new ProgramListDownloader();
//		List<Program> allPrograms = progDownloader.getWeeklyProgramList(
//				stationListDownloader.getStationList(areas, LogoSize.XSMALL, new DefaultHttpClient()), new DefaultHttpClient());
//	
//		List<String> keyWords = new ArrayList<String>();
//		keyWords.add("AKB48");
//		
//		mTarget = new RecommendProgramCreator();
//		List<Program> recommends = mTarget.createRecommendProgram(allPrograms, keyWords);
//		
//		assertTrue(0 < recommends.size());
//		assertFalse(allPrograms.size() == recommends.size());
//		
//		// ソートされているかチェック。
//		for(int i=0; i<recommends.size()-1; i++) {
//			long lstart = Long.valueOf(recommends.get(i).start);
//			long rstart = Long.valueOf(recommends.get(i+1).start);
//			assertTrue(lstart <= rstart);
//		}
//		
//	}

}
