/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import tsuyogoro.sugorokuon.datatype.Program;

/**
 * Parse���鎞�_�Ō��ʂ�filter�������邽�߂ɗ��p����B
 * 
 */

public interface IProgramListParserFilter {

	public boolean isRecommend(Program p);
}
