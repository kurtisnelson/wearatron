package com.thisisnotajoke.lockitron;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Lock implements Serializable{
    /*
    {
  "id": "ba41debd-d8b1-4423-b828-1b7d636ce07a",
  "name": "test",
  "next_wake": null,
  "state": "lock",
  "button_type": "slider",
  "updated_at": "2014-09-09T06:00:03Z",
  "handedness": "unsupported",
  "sleep_period": null,
  "avr_update_progress": null,
  "ble_update_progress": null,
  "pending_activity": {},
  "serial_number": "DLOAR-YBFUY-NVFTV-UPTSXKC",
  "time_zone": null,
  "keys": [],
  "sms": false
}
     */
    @SerializedName("id")
    private String uuid;
    @SerializedName("name")
    private String name;
    @SerializedName("state")
    private String state;
    @SerializedName("serial_number")
    private String serialNumber;
    @SerializedName("updated_at")
    private DateTime updatedAt;

    public Lock() {
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
