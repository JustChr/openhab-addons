# Zodiac Pool Binding

The `zodiacpool` binding integrates Zodiac Pool devices into OpenHAB, allowing users to monitor and control various pool parameters such as temperature, pH level, status, and filter pump state.

## Overview

The Zodiac Pool binding allows you to connect your Zodiac Pool Controller to OpenHAB, enabling real-time monitoring and control of your pool environment from the OpenHAB interface. This includes viewing the pool’s temperature, pH level, operational status, and filter pump state.

## Supported Devices

- Zodiac Pool Controller

## Thing Configuration

### Zodiac Pool Controller Thing

To configure the Zodiac Pool Controller, you will need the following information:

- **Device ID**: The unique identifier of your Zodiac Pool device.
- **Email**: The email address associated with your Zodiac Pool account.
- **Password**: The password for your Zodiac Pool account.
- **API Key**: The API key provided by Zodiac for accessing the service.

## Channels

The following channels are available for the Zodiac Pool Controller thing:

| Channel ID       | Label             | Description                                      | Type      |
|------------------|-------------------|--------------------------------------------------|-----------|
| `poolTemperature`| Pool Temperature  | The current temperature of the pool water.       | Number    |
| `poolPH`         | Pool pH Level     | The current pH level of the pool water.          | Number    |
| `poolStatus`     | Pool Status       | The current operational status of the pool.      | String    |
| `statusTimestamp`| Status Timestamp  | The last timestamp when the status was updated.  | DateTime  |
| `filterPump`     | Filter Pump State | Indicates if the filter pump is active or idle.  | String    |

## Thing Configuration Parameters

The following configuration parameters are required to set up the Zodiac Pool Controller:

| Parameter        | Required | Description                                                    |
|------------------|----------|----------------------------------------------------------------|
| `deviceId`       | Yes      | The unique identifier of your Zodiac Pool device.             |
| `email`          | Yes      | The email associated with your Zodiac Pool account.           |
| `password`       | Yes      | The password for your Zodiac Pool account.                    |
| `apiKey`         | Yes      | The API key provided by Zodiac for authentication.            |
| `refreshInterval`| No       | Interval in seconds for refreshing data (default is 60 sec).  |

## Example Thing Configuration

Here is an example configuration for the Zodiac Pool Controller thing:

```java
Thing zodiacpool:zodiacpoolcontroller:myPool "My Pool" @ "Home" [ 
    deviceId="YOUR_DEVICE_ID",
    email="YOUR_EMAIL",
    password="YOUR_PASSWORD",
    apiKey="YOUR_API_KEY",
    refreshInterval=60
]
