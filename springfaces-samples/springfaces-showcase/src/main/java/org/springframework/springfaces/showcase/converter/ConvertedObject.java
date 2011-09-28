package org.springframework.springfaces.showcase.converter;

/**
 * Example object used to demonstrate converstion.
 * 
 * @author Phillip Webb
 */
public class ConvertedObject {

	private String from;
	private long value;

	public ConvertedObject(String from, long value) {
		this.from = from;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value) + " from " + from;
	}

	public String getFrom() {
		return from;
	}

	public long getValue() {
		return value;
	}
}