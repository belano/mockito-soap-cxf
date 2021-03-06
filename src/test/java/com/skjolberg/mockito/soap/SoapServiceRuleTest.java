package com.skjolberg.mockito.soap;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.cxf.helpers.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.matchers.Contains;

import com.skjolberg.example.spring.soap.v1.BankCustomerServicePortType;

public class SoapServiceRuleTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Rule
	public SoapServiceRule soap = SoapServiceRule.newInstance();

	@Test
	public void testInvalidParameters1() {
		exception.expect(IllegalArgumentException.class);

		soap.mock(BankCustomerServicePortType.class, null);
	}
	
	@Test
	public void testInvalidParameters2() {
		exception.expect(IllegalArgumentException.class);

		soap.mock(BankCustomerServicePortType.class, "http://localhost:12345", new ArrayList<String>());
	}

	@Test
	public void testInvalidParameters3() {
		exception.expect(IllegalArgumentException.class);

		soap.mock(BankCustomerServicePortType.class, "http://localhost:12345", "");
	}
	
	@Test
	public void testWSDL() throws Exception {
		String address = "http://localhost:12345/service";
		
		BankCustomerServicePortType mock = soap.mock(BankCustomerServicePortType.class, address);
		
		URL url = new URL(address + "?wsdl");
		
		String wsdl = IOUtils.toString(url.openStream());

		assertThat(wsdl, containsString("wsdl:definitions"));
	}
	
	/**
	 * Test that stop and (re)start works - simulate service offline down.
	 * 
	 * @throws Exception
	 */
	
	@Test
	public void testEndpointStartStop() throws Exception {
		String address = "http://localhost:12345/service";
		
		soap.mock(BankCustomerServicePortType.class, address);
		
		URL url = new URL(address + "?wsdl");
		
		soap.stop();
		
		try {
			url.openStream();
			
			Assert.fail();
		} catch(FileNotFoundException e) {
			// pass
		}
		
		soap.start();
		
		String wsdl = IOUtils.toString(url.openStream());

		assertThat(wsdl, containsString("wsdl:definitions"));
	}
	

}
