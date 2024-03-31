package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于存放碳排放数据表中的分排放额
 * @author FALSHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonAccountingEmissionsVolumeDO {
    Material materials;
    Material courses;
    Material carbonSequestrations;
    Material desulfuizations;
    Heat heat;
    Electric electric;

    @Data
    @Accessors(chain = true)
    public static class Material {
        public String name;
        public Double carbonEmissions;
    }
    @Data
    @Accessors(chain = true)
    public static class Heat{
        public String name;
        public Double heatEmissions;
    }

    @Data
    @Accessors(chain = true)
    public static class Electric {
        public String name;
        public Double electricEmissions;
    }

}
