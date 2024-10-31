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

/**
 * The {@link ZodiacPoolConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Christoph K - Initial contribution
 */
@NonNullByDefault
public class ZodiacPoolConfiguration {

    @Parameter(name = "deviceId")
    public String deviceId;

    @Parameter(name = "email")
    public String email;

    @Parameter(name = "password")
    public String password;

    @Parameter(name = "apiKey", defaultValue = "EOOEMOW4YR6QNB11")
    public String apiKey;
}
