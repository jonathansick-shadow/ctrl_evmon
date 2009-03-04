package lsst.ctrl.evmon.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class IndexedMultiMap extends MultiMap {
	int lastEntry = -1;

	class IndexedEntry {
		int index;
		Object obj;

		public IndexedEntry(int index, Object obj) {
			this.index = index;
			this.obj = obj;
		}

		public int getIndex() {
			return index;
		}

		public Object getValue() {
			return obj;
		}
	}

	public void put(String key, Object obj) {
		IndexedEntry ie = new IndexedEntry(lastEntry++, obj);
		super.put(key, ie);
	}

	public Object get(String key) {
		List l = map.get(key);
		if (l == null)
			return null;
		IndexedEntry ie = (IndexedEntry) l.get(0);
		return ie.getValue();
	}

	public Collection getAll(String key) {
		List internalList = map.get(key);
		ArrayList arrayList = new ArrayList();

		for (int i = 0; i < internalList.size(); i++) {
			IndexedEntry ie = (IndexedEntry) internalList.get(i);
			arrayList.add(ie.getValue());
		}
		return arrayList;
	}

	public Collection getOrderedCollection() {
		Set set = map.entrySet();

		TreeMap treeMap = new TreeMap();

		Iterator it = set.iterator();

		while (it.hasNext()) {
			List list = (List) it.next();

			for (int i = 0; i < list.size(); i++) {
				IndexedEntry ie = (IndexedEntry) list.get(i);
				int index = ie.getIndex();
				Object obj = ie.getValue();
				treeMap.put(index, obj);
			}
		}
		return treeMap.values();
	}
}
