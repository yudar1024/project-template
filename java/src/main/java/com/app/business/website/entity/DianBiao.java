package com.app.business.website.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 电表设备 实体类。
 *
 * @author CodeGenerator
 * @since 2025-11-24
 */
@Table(value = "tb_dian_biao", schema = "vpp", dataSource = "ds2")
public class DianBiao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
    private Timestamp time;

    @Id
    private String deviceId;

    private String maintenance;

    private Float temperature;

    private Float elecUsed;

    private Boolean status;

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getElecUsed() {
        return elecUsed;
    }

    public void setElecUsed(Float elecUsed) {
        this.elecUsed = elecUsed;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
