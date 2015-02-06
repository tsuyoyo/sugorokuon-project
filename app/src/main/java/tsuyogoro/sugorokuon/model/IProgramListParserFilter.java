/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import tsuyogoro.sugorokuon.datatype.Program;

/**
 * Parseする時点で結果にfilterをかけるために利用する。
 *
 */

public interface IProgramListParserFilter {

	public boolean isRecommend(Program p);
}
