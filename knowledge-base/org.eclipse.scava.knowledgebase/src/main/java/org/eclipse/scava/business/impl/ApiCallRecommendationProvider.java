/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.business.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.scava.business.ICodeCloneDetector;
import org.eclipse.scava.business.IRecommendationProvider;
import org.eclipse.scava.business.dto.ApiCallResult;
import org.eclipse.scava.business.dto.Query;
import org.eclipse.scava.business.dto.Recommendation;
import org.eclipse.scava.business.dto.RecommendationItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.harukizaemon.simian.Language;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;

/**
 * @author Juri Di Rocco
 *
 */
@Service
@Qualifier("ApiCallRecommendation")
public class ApiCallRecommendationProvider implements IRecommendationProvider {

	private static final Logger logger = Logger.getLogger(ApiCallRecommendationProvider.class);

	@Value("${api.call.recommendation.path}")
	private String pattern;

	@Autowired
	private ICodeCloneDetector codeCloneDetector;

	@Override
	public Recommendation getRecommendation(Query query) throws Exception {
		Recommendation rec = new Recommendation();
		List<ApiCallResult> result = new ArrayList<>();
		long start = System.currentTimeMillis();

		ArrayList<String> patterns = scan();
		Options options = new Options();
		options.setThreshold(3);
		options.setOption(Option.REPORT_DUPLICATE_TEXT, true);
		options.setOption(Option.IGNORE_STRINGS, true);
		options.setOption(Option.IGNORE_STRING_CASE, true);
		options.setOption(Option.IGNORE_VARIABLE_NAMES, true);
		options.setOption(Option.IGNORE_CHARACTER_CASE, true);
		options.setOption(Option.IGNORE_IDENTIFIER_CASE, true);
		options.setOption(Option.IGNORE_MODIFIERS, true);
		options.setOption(Option.IGNORE_LITERALS, true);
		options.setOption(Option.LANGUAGE, Language.JAVA);
		options.setOption(Option.IGNORE_SUBTYPE_NAMES, true);

		for (String pattern : patterns) {
			ApiCallResult k = codeCloneDetector.checkClone(query.getMethodInvocation(), pattern, options);
			if (k.getCodeLines() != null && k.getCodeLines().size() > 0)
				result.add(k);
		}
		long end = System.currentTimeMillis();
		long elapse = end - start;
		logger.info("Pattern are found in  " + elapse + " ms");
		Collections.sort(result, new PatternComparator());
		for (ApiCallResult apiCall : result) {
			RecommendationItem ri = new RecommendationItem();
			ri.setApiCallRecommendation(apiCall);
			rec.getRecommendationItems().add(ri);
		}
		return rec;
	}

	private ArrayList<String> scan() {
		ArrayList<String> result = new ArrayList<>();
		ClassLoader classLoader = getClass().getClassLoader();
		File rank = new File(classLoader.getResource(pattern).getFile());
		File[] list = rank.listFiles();
		for (File f : list)
			if (f.isFile()) {
				String s;
				try {
					s = FileUtils.readFileToString(f);
					result.add(s);
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		return result;
	}

}

class PatternComparator implements Comparator<ApiCallResult> {
	@Override
	public int compare(ApiCallResult o1, ApiCallResult o2) {
		return o2.getDuplicatedLines() - o1.getDuplicatedLines();
	}
}