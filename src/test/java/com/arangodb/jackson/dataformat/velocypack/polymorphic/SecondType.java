package com.arangodb.jackson.dataformat.velocypack.polymorphic;

import java.util.Objects;

public class SecondType implements PolyType {

	private String value;

	public SecondType() {
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SecondType that = (SecondType) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "SecondType{" + "value='" + value + '\'' + '}';
	}

}
