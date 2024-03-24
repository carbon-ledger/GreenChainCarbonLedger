package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于粗放碳排放数据表中的分排放额
 * @author FALSHLACK
 */
@Data
@Accessors(chain = true)
public class CarbonAccountingEmissionsVolumeDO {
    Materials materials;
    Courses courses;
    CarbonSequestration carbonSequestrations;
    Desulfuization desulfuizations;

    @Data
    @Accessors(chain = true)
    public static class Materials {
        public String name;
        public Double carbonEmissions;
    }
    @Data
    @Accessors(chain = true)
    public static class Courses {
        public String name;
        public Double carbonEmissions;
    }
    @Data
    @Accessors(chain = true)
    public static class CarbonSequestration {
        public String name;
        public Double carbonEmissions;
    }
    @Data
    @Accessors(chain = true)
    public static class Desulfuization {
        public String name;
        public Double carbonEmissions;
    }

}
