package org.apache.maven.plugin.github.stubs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author <a href="mailto:marcelo.riss@gmail.com">Marcelo Riss</a>
 * @version 2018 Oct, 4
 */
public class SettingsStub extends Settings {

	public SettingsStub() {
		super();
		setProxies(readProxies());
		setServers(readServers());
	}

	private List<Server> readServers() {
		String userHome = System.getProperty("user.home");
		if (userHome != null && !userHome.isEmpty()) {
			try {
				FileReader sReader = new FileReader(new File(userHome + File.separator + ".m2" + File.separator + "settings.xml"));
				SettingsXpp3Reader modelReader = new SettingsXpp3Reader();
				Settings settings = modelReader.read( sReader, true );
				return settings.getServers();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}            			
		}
		
		return null;
	}

	private List<Proxy> readProxies() {		
		String mavenHome = System.getenv("M2_HOME");
		if (mavenHome != null && !mavenHome.isEmpty()) {
			try {
				List<Proxy> proxies = getProxiesFromSettings(mavenHome, "settings.xml");
				if (isProxyEnvironment()) {
					proxies = new ArrayList<Proxy>();
					String httpProxy = System.getenv("http_proxy");
					if (httpProxy != null) {
						Proxy p = new Proxy();
						URL u = new URL(httpProxy);
						p.setHost(u.getHost());
						p.setProtocol(u.getProtocol());
						p.setPort(u.getPort());
						proxies.add(p);
					}
					String httpsProxy = System.getenv("http_proxy");
					if (httpsProxy != null) {
						Proxy p = new Proxy();
						URL u = new URL(httpsProxy);
						p.setHost(u.getHost());
						p.setProtocol(u.getProtocol());
						p.setPort(u.getPort());
						proxies.add(p);
					}					
				}
				return proxies;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}            
		} else {
			List<Proxy> result = new ArrayList<Proxy>();
			return result;
		}		
	}

	private boolean isProxyEnvironment() {
		boolean ishttpProxy = System.getenv("http_proxy") != null && !System.getenv("http_proxy").isEmpty();
		boolean ishttpsProxy = System.getenv("https_proxy") != null && !System.getenv("https_proxy").isEmpty();
		boolean isHTTPProxy = System.getenv("HTTP_PROXY") != null && !System.getenv("HTTP_PROXY").isEmpty();
		boolean isHTTPSProxy = System.getenv("HTTPS_PROXY") != null && !System.getenv("HTTPS_PROXY").isEmpty();
		return (ishttpProxy || ishttpsProxy || isHTTPProxy || isHTTPSProxy);
	}

	private List<Proxy> getProxiesFromSettings(String mavenHome, String settingsFileName)
			throws FileNotFoundException, IOException, XmlPullParserException {
		FileReader sReader = new FileReader(new File(mavenHome + "/conf/" + settingsFileName));
		SettingsXpp3Reader modelReader = new SettingsXpp3Reader();
		Settings settings = modelReader.read( sReader, true );
		List<Proxy> proxies = settings.getProxies();
		return proxies;
	}

}
