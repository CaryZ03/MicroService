package com.dofinal.RG.entity.location;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname Location
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 19:19
 * &#064;Created MuJue
 */
@TableName("locations")
public class Location {
    @TableField("l_id")
    private int lId;
    @TableField("province")
    private String province;
    @TableField("city")
    private String city;

    public Location(int lId, String province, String city) {
        this.lId = lId;
        this.province = province;
        this.city = city;
    }
    public Location(){;}
    public int getlId() {
        return lId;
    }

    public void setlId(int lId) {
        this.lId = lId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
