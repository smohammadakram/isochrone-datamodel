package com.inf.unibz.parser;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSFDatabaseParser {
	
	private SFDatabaseParser obj;

	@Before
	public void setUp() throws Exception {
		obj = new SFDatabaseParser();
	}

	@After
	public void tearDown() throws Exception {
		obj = null;
	}
	
	@Test
	public void getInsertColumnsTest(){
		String line = "COPY sf_edges (length, route_id, source_outdegree, source_c_outdegree, geometry) FROM stdin;";
		String result = obj.getInsertColumns(line);
		assertEquals("(length, route_id, source_outdegree, source_c_outdegree, geometry)", result);
	}
	
	@Test
	public void getDataRowTest(){
		String line = "64.269999999999996	NULL	4	4";
		String result = obj.getDataRow(line);
		System.out.println(result);
		assertEquals("('64.269999999999996', '',	'4', '4')", result);
	}

}
