/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.zodiacpool.internal;

import static org.openhab.binding.zodiacpool.internal.ZodiacPoolBindingConstants.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ZodiacPoolHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Christoph K - Initial contribution
 */
public class ZodiacPoolHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(ZodiacPoolHandler.class);

    private String deviceId;
    private String email;
    private String password;
    private String apiKey;
    private ScheduledExecutorService scheduler;
    private int refreshInterval; // in seconds

    public ZodiacPoolHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        ZodiacPoolConfiguration config = getThing().getConfiguration().as(ZodiacPoolConfiguration.class);

        deviceId = (String) config.deviceId;
        email = (String) config.email;
        password = (String) config.password;
        apiKey = (String) config.apiKey;
        refreshInterval = config.refreshInterval;

        if (deviceId == null || email == null || password == null || apiKey == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Missing configuration parameters");
        } else {
            updateStatus(ThingStatus.ONLINE); // Set Thing status to ONLINE
            updatePoolData(); // Initial data fetch
            startDataRefresh();
        }
    }

    private void startDataRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updatePoolData, 0, refreshInterval, TimeUnit.SECONDS);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updatePoolData();
        }
    }

    private void updatePoolData() {
        try {
            String token = authenticate();
            if (token != null) {
                fetchPoolData(token);
            }
        } catch (Exception e) {
            logger.error("Error updating pool data: {}", e.getMessage());
        }
    }

    private String authenticate() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://prod.zodiac-io.com/users/v1/login"))
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{\"api_key\":\""
                        + apiKey + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return new JSONObject(response.body()).getJSONObject("userPoolOAuth").getString("IdToken");
        } else {
            logger.warn("Failed to authenticate: {}", response.statusCode());
            return null;
        }
    }

    private void fetchPoolData(String token) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://prod.zodiac-io.com/devices/v1/" + deviceId + "/shadow"))
                .header("Authorization", token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject obj = new JSONObject(response.body());
            double poolTemp = obj.getJSONObject("state").getJSONObject("reported").getJSONObject("equipment")
                    .getJSONObject("swc_0").getJSONObject("sns_3").getDouble("value");

            double poolPH = obj.getJSONObject("state").getJSONObject("reported").getJSONObject("equipment")
                    .getJSONObject("swc_0").getJSONObject("sns_1").getDouble("value") / 10.0;

            String poolState = obj.getJSONObject("state").getJSONObject("reported").getJSONObject("aws")
                    .getString("status");
            long timestamp = obj.getJSONObject("state").getJSONObject("reported").getJSONObject("aws")
                    .getLong("timestamp") / 1000;
            Instant statusTimestamp = Instant.ofEpochSecond(timestamp);
            String filterPumpState = obj.getJSONObject("state").getJSONObject("reported").getJSONObject("equipment")
                    .getJSONObject("swc_0").getJSONObject("filter_pump").getString("state");

            // Update each Channel
            updateState(new ChannelUID(getThing().getUID(), "poolTemperature"), new DecimalType(poolTemp));
            updateState(new ChannelUID(getThing().getUID(), "poolPH"), new DecimalType(poolPH));
            updateState(new ChannelUID(getThing().getUID(), "poolStatus"), new StringType(poolState));
            updateState(new ChannelUID(getThing().getUID(), "statusTimestamp"),
                    new DateTimeType(statusTimestamp.toString()));
            updateState(new ChannelUID(getThing().getUID(), "filterPump"), new StringType(filterPumpState));

        } else {
            logger.warn("Failed to fetch pool data: {}", response.statusCode());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
