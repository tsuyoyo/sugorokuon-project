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
//	 * String��parse��test�B
//	 * 
//	 */
//	@LargeTest
//	public void testParse_ParseStringTest_01() {
//		String target = "��ɂ�����A�C�h���g�`�j�a�S�W�h�ɁA���T���j���̃I�[���i�C�g�j�b�|���ł����I<br>����ǂ��A���񃁃��o�[�V���b�t���ł����肷��`�j�a�S�W�̃I�[���i�C�g�j�b�|���I�I";
//		String keyword = "AKB48";
//		String zenkaku = "�`�j�a�S�W";
//		assertFalse(target.matches(".*" + keyword + ".*"));
//		assertTrue(target.matches(".*" + zenkaku + ".*"));
//	}
//	
//	/**
//	 * AKB48�I�^�ɃI�X�X���̔ԑg�����(�j�b�|����������)�B
//	 * 
//	 */
//	@LargeTest
//	public void testGetRecommendProgramsForAKBOta_01() {
//
//		// �j�b�|���������痎�Ƃ�
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
//	 * AKB48�I�^�ɃI�X�X���̔ԑg�����(�֓��S�悩��)�B
//	 * 
//	 */
//	@LargeTest
//	public void testGetRecommendProgramsForAKBOta_02() {
//
//		// �֓��S��G���A
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
//		// �\�[�g����Ă��邩�`�F�b�N�B
//		for(int i=0; i<recommends.size()-1; i++) {
//			long lstart = Long.valueOf(recommends.get(i).start);
//			long rstart = Long.valueOf(recommends.get(i+1).start);
//			assertTrue(lstart <= rstart);
//		}
//		
//	}

}
