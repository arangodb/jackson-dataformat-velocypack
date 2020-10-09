package com.fasterxml.jackson.databind.big;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.databind.*;

public class TestBiggerData extends BaseMapTest
{
	static class Citm
	{
		public Map<Integer,String> areaNames;
		public Map<Integer,String> audienceSubCategoryNames;
		public Map<Integer,String> blockNames;
		public Map<Integer,String> seatCategoryNames;
		public Map<Integer,String> subTopicNames;
		public Map<Integer,String> subjectNames;
		public Map<Integer,String> topicNames;
		public Map<Integer,int[]> topicSubTopics;
		public Map<String,String> venueNames;

		public Map<Integer,Event> events;
		public List<Performance> performances;
	}

	static class Event
	{
		public int id;
		public String name;
		public String description;
		public String subtitle;
		public String logo;
		public int subjectCode;
		public int[] topicIds;
		public LinkedHashSet<Integer> subTopicIds;
	}

	static class Performance
	{
		public int id;
		public int eventId;
		public String name;
		public String description;
		public String logo;

		public List<Price> prices;
		public List<SeatCategory> seatCategories;

		public long start;
		public String seatMapImage;
		public String venueCode;
}

	static class Price {
		public int amount;
		public int audienceSubCategoryId;
		public int seatCategoryId;
	}

	static class SeatCategory {
		public int seatCategoryId;
		public List<Area> areas;
	}

	static class Area {
		public int areaId;
		public int[] blockIds;
	}

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

	private final ObjectMapper MAPPER = newJsonMapper();

	public void testReading() throws Exception
	{
		String json = new String(Files.readAllBytes(Paths.get(getClass().getResource("/data/citm_catalog.json").toURI())));

		Citm citm = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(json),
				Citm.class);
		assertNotNull(citm);
		assertNotNull(citm.areaNames);
		assertEquals(17, citm.areaNames.size());
		assertNotNull(citm.events);
		assertEquals(184, citm.events.size());

		assertNotNull(citm.seatCategoryNames);
		assertEquals(64, citm.seatCategoryNames.size());
		assertNotNull(citm.subTopicNames);
		assertEquals(19, citm.subTopicNames.size());
		assertNotNull(citm.subjectNames);
		assertEquals(0, citm.subjectNames.size());
		assertNotNull(citm.topicNames);
		assertEquals(4, citm.topicNames.size());
		assertNotNull(citm.topicSubTopics);
		assertEquals(4, citm.topicSubTopics.size());
		assertNotNull(citm.venueNames);
		assertEquals(1, citm.venueNames.size());
	}

	public void testRoundTrip() throws Exception
	{
		String json = new String(Files.readAllBytes(Paths.get(getClass().getResource("/data/citm_catalog.json").toURI())));
		Citm citm = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(json),
				Citm.class);

		ObjectWriter w = MAPPER.writerWithDefaultPrettyPrinter();
		
		String json1 = com.fasterxml.jackson.VPackUtils.toJson( w.writeValueAsBytes(citm));
		Citm citm2 = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(json1), Citm.class);
		String json2 = com.fasterxml.jackson.VPackUtils.toJson( w.writeValueAsBytes(citm2));

		assertEquals(json1, json2);
	}
}
