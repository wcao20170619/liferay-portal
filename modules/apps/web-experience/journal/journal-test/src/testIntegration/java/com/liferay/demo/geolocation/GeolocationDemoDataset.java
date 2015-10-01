package com.liferay.demo.geolocation;

public class GeolocationDemoDataset {

	interface Visitor {

		void visit(Request311 entry) throws Exception;

	}

	GeolocationDemoDataset(Request311[] entries) {
		this.entries = entries;
	}

	void accept(Visitor visitor) throws Exception {
		for (Request311 entry : entries) {
			visitor.visit(entry);
		}
	}

	Request311[] entries;

	public int size() {
		return entries.length;
	}

}
