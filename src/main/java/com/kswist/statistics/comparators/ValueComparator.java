package com.kswist.statistics.comparators;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class ValueComparator<K, V extends Comparable<V>> implements Comparator<Map.Entry<K,V>>{
	
	@Override
	public int compare(Entry<K, V> arg0, Entry<K, V> arg1) {
		return arg1.getValue().compareTo(arg0.getValue());
	}

}
