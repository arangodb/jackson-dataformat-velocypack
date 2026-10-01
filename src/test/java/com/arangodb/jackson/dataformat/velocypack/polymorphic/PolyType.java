package com.arangodb.jackson.dataformat.velocypack.polymorphic;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Michele Rastelli
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
			  include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
			  property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(name = "FirstType",
								   value = FirstType.class),
					  @JsonSubTypes.Type(name = "SecondType",
										 value = SecondType.class) })
public interface PolyType {

	String getValue();
}
