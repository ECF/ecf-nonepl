package org.remotercp.util.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

public class TestXMLConverter {

	private Person person;

	@Before
	public void setupObjects() {
		person = new Person("susi");
	}

	@Test
	public void testXMLConverter() {
		XStream stream = new XStream();
		String xml = stream.toXML(person);
		assertNotNull(xml);

		Object obj = stream.fromXML(xml);
		if (obj instanceof Person) {
			Person p = (Person) obj;
			assertEquals("susi", p.getName());
		} else {
			fail("The converted object is instance of an unknown type");
		}
	}


	private class Person {
		String name;

		public Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
}
