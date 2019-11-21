package com.cisco.pxgrid.samples.ise;

import java.time.OffsetDateTime;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.pxgrid.samples.ise.model.AccountState;
import com.cisco.pxgrid.samples.ise.model.Service;

/**
 * Demonstrates how to query all sessions from ISE Session Directory service
 */
public class SessionQueryAll {
	private static Logger logger = LoggerFactory.getLogger(SessionQueryAll.class);
	
	private static class SessionQueryRequest {
		OffsetDateTime startTimestamp;
	}

	private static void downloadUsingAccessSecret(SampleConfiguration config) throws Exception {
		OffsetDateTime startTimestamp = SampleHelper.promptDate("Enter start time (ex. '2015-01-31T13:00:00-07:00' or <enter> for no start time): ");
		
		PxgridControl https = new PxgridControl(config);
		
		// pxGrid ServiceLookup for session service
		Service[] services = https.serviceLookup("com.cisco.ise.session");
		if (services == null || services.length == 0) {
			logger.warn("Service unavailabe");
			return;
		}
		
		// Use first service
		Service service = services[0];
		String url = service.getProperties().get("restBaseUrl") + "/getSessions";
		logger.info("url={}", url);
		
		// pxGrid AccesssSecret for the node
		String secret = https.getAccessSecret(service.getNodeName());

		SessionQueryRequest request = new SessionQueryRequest();
		request.startTimestamp = startTimestamp;
		SampleHelper.postObjectAndPrint(url, config.getNodeName(), secret, config.getSSLContext().getSocketFactory(), request);
	}

	public static void main(String [] args) throws Exception {
		// Parse arguments
		SampleConfiguration config = new SampleConfiguration();
		try {
			config.parse(args);
		} catch (ParseException e) {
			config.printHelp("SessionQueryAll");
			System.exit(1);
		}

		// AccountActivate
		PxgridControl control = new PxgridControl(config);
		while (control.accountActivate() != AccountState.ENABLED)
			Thread.sleep(60000);
		logger.info("pxGrid controller version={}", control.getControllerVersion());

		downloadUsingAccessSecret(config);
	}
}
