package com.kswist.statistics.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.kswist.statistics.db.entities.User;

public class ValueComparator
		implements Comparator<Map.Entry<User, Map<String, Integer>>> {

	@Override
	public int compare(Entry<User, Map<String, Integer>> o1,
			Entry<User, Map<String, Integer>> o2) {
		Set<Map.Entry<String, Integer>> set = o1.getValue().entrySet();
		String date = new ArrayList<Map.Entry<String, Integer>>(set)
				.get(set.size() - 1).getKey();
		return o2.getValue().get(date).compareTo(o1.getValue().get(date));
	}
}