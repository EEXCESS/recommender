/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.eexcess.opensearch.recommender.searchLink;

/**
 * A filter that can be applied by {@link SearchLinkSelector}.
 * 
 * @author Raoul Rubien
 */
public class SearchLinkFilter {

//	private String method = null;
//	private boolean isMethodActive = false;

	private String type = null;
	private boolean isTypeActive = false;

//	/**
//	 * @param method to be compared to link method (GET, POST)
//	 */
//	public void setMethod(String method) {
//		this.method = new String(method);
//		isMethodActive = true;
//	}

//	/**
//	 * reset method argument
//	 */
//	public void clearMethod() {
//		method = null;
//		isMethodActive = false;
//	}

	/**
	 * @param mimeType to be compared to the link type
	 */
	public void setType(String mimeType) {
		this.type = new String(mimeType);
		isTypeActive = true;
	}

	/**
	 * reset MimeType argument
	 */
	public void clearType() {
		type = null;
		isTypeActive = false;
	}

	String getType() {
		return type;
	}

	/**
	 * @return true if type has been set
	 */
	boolean isTypeActive() {
		return isTypeActive;
	}

//	String getMethod() {
//		return method;
//	}

//	/**
//	 * @return true if method has been set
//	 */
//	boolean isMethodActive() {
//		return isMethodActive;
//	}
}
