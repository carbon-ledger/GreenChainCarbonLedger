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
    Materials materials;
    Courses courses;
    CarbonSequestration carbonSequestrations;
    Heat heat;
    Desulfuization desulfuizations;
    Electric electric;

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
    public static class Heat{
        public String name;
        public Double heatEmissions;
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

    @Data
    @Accessors(chain = true)
    public static class Electric {
        public String name;
        public Double electricEmissions;
    }

}
