/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.graph_builder.model;

import org.opentripplanner.util.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.*;

import org.springframework.beans.factory.InitializingBean;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GtfsExchangeBundles extends GtfsBundles implements InitializingBean {

  private static final Logger _log = LoggerFactory.getLogger(GtfsExchangeBundles.class);

  private List<GtfsBundle> bundles = new ArrayList<GtfsBundle>();
  private List<String> agencyBlacklist = new ArrayList<String>();

  public List<GtfsBundle> getBundles() { return bundles; }
  public void setBundles(List<GtfsBundle> bundles) { this.bundles = bundles; }

  public List<String> getAgencyBlacklist() { return agencyBlacklist; }
  public void setAgencyBlacklist(List<String> list) { agencyBlacklist = list; }


  public void afterPropertiesSet() throws Exception {
    // Begin - get the list of agencies
    String agenciesURL = "http://www.gtfs-data-exchange.com/api/agencies?format=json";
    String agenciesJson = getJson(agenciesURL);
    JSONObject agenciesObj = (JSONObject)parseJson(agenciesJson);
    JSONArray agencies = (JSONArray)agenciesObj.get("data");
    for (Object agency : agencies) {
      JSONObject a = (JSONObject)agency;
      String id = (String)a.get("dataexchange_id");
      String directUrl = (String)a.get("feedbase_url");
      String exchangeUrl = (String)a.get("dataexchange_url") + "latest.zip";
      String country = (String)a.get("country");
      Boolean isOfficial = (Boolean)a.get("is_official");

      Boolean includeRegion = (country.equalsIgnoreCase("United States") || country.equalsIgnoreCase("Canada"));
      if (isOfficial && !isBlacklisted(id) && includeRegion) {
        System.out.println("Adding GTFS bundle to list for agency '" + id + "' and url '" + exchangeUrl + "'");
        GtfsBundle b = new GtfsBundle();
        b.setUrl(new URL(exchangeUrl));
        b.setDefaultAgencyId(id);
        bundles.add(b);
      }
    }
  }

  private String getJson(String url) throws IOException {
    InputStream is = HttpUtils.getData(url);
    return convertStreamToString(is);
  }

  private Object parseJson(String json) {
    return (Object)JSONValue.parse(json);
  }

  private String convertStreamToString(InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  private Boolean isBlacklisted(String agencyId) {
    for (String blacklisted : agencyBlacklist) {
      if (agencyId.equalsIgnoreCase(blacklisted)) { return true; }
    }
    return false;
  }
}
