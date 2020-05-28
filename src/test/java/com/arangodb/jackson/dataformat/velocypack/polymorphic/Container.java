package com.arangodb.jackson.dataformat.velocypack.polymorphic;

import java.util.Map;
import java.util.Objects;

public class Container {
	private Map<String, PolyType> attributes;
	private String text;

	public Container() {
	}

	public Map<String, PolyType> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, PolyType> attributes) {
		this.attributes = attributes;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Container container = (Container) o;
		return Objects.equals(attributes, container.attributes) && Objects.equals(text, container.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(attributes, text);
	}

	@Override
	public String toString() {
		return "Container{" + "attributes=" + attributes + ", text='" + text + '\'' + '}';
	}

}
