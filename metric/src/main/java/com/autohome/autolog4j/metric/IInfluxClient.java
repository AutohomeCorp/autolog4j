package com.autohome.autolog4j.metric;

import com.autohome.autolog4j.metric.contract.InfluxPoint;

/**
 * Created by NFW on 2018/8/2.
 */
public interface IInfluxClient {
    /**
     * Write a single Point to the influxdb.
     *
     * @param point The point to write
     */
    void write(InfluxPoint point);

    /**
     * Send Lineprotocol Point to the influxdb.
     *
     * @param line the lineprotocol value to be sended
     */
    void write(String line);
}
