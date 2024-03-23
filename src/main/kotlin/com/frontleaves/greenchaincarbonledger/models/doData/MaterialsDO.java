package com.frontleaves.greenchaincarbonledger.models.doData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
/**
 * E碳的燃料种类和消耗量
 *
 * @author FLASHLACK
 */
@Data
@Accessors(chain = true)
public class MaterialsDO {
    List<Materials> materials;
    List<Courses> courses;

    /**
     * E燃烧的种类和消耗量
     */
    @Data
    @Accessors(chain = true)
    public static class Materials {
        public String name;
        public Material material;
    }

    /**
     * E过程的种类和消耗量
     */

    @Data
    @Accessors(chain = true)
    public static class Courses {
        public String name;
        public Material material;
    }

    /**
     * R固碳的种类和小号量
     */

    @Data
    @Accessors(chain = true)
    public static class CarbonSequestration {
        public String name;
        public MaterialSequestration material;

        @Data
        @Accessors(chain = true)
        public static class MaterialSequestration {
            public String openingInv;
            public String endingInv;
            public String export;
        }
    }


    @Data
    @Accessors(chain = true)
    public static class Material {
        public String buy;
        public String openingInv;
        public String endingInv;
        public String outSide;
        public String export;
    }
}
