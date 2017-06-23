package databaseImplementations;

import databaseInterfaces.IDatabaseElement;

public class DataBaseElement<Key extends Comparable<Key>, Value> implements IDatabaseElement<Key, Value> {

	final private Key key;
	final private Value vlue;

	public DataBaseElement(Key key, Value value) {
		this.key = key;
		this.vlue = value;
	}

	@Override
	public Key getKey() {
		return this.key;
	}

	@Override
	public Value getValue() {
		return this.vlue;
	}

}
