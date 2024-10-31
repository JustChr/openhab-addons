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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link ZodiacPoolBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Christoph K - Initial contribution
 */
@NonNullByDefault
public class ZodiacPoolBindingConstants {

    private static final String BINDING_ID = "zodiacpool";

    public static final String THING_TYPE_ZODIACPOOL = "zodiacpool";

    // Channel IDs
    public static final String CHANNEL_POOL_TEMPERATURE = "poolTemperature";
    public static final String CHANNEL_POOL_PH = "poolPH";
    public static final String CHANNEL_POOL_STATUS = "poolStatus";
    public static final String CHANNEL_STATUS_TIMESTAMP = "statusTimestamp";
    public static final String CHANNEL_FILTER_PUMP = "filterPump";
}
