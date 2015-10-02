package com.kswist.statistics.comparators;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.kswist.statistics.db.entities.User;

public class UserComparator implements Comparator<Map.Entry<User, Integer>> {

	@Override
	public int compare(Entry<User, Integer> o2, Entry<User, Integer> o1) {

		return o2.getValue().compareTo(o1.getValue());
	}
}