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
    // E燃烧参数
    List<Materials> materials;
    // E过程参数
    List<Courses> courses;
    // E固碳参数
    List<CarbonSequestration> carbonSequestrations;
    // E脱硫参数
    List<Desulfurization> desulfurization;
    // E热力参数
    List<Heat> heat;

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
     * E脱硫的种类和消耗量
     */
    @Data
    @Accessors(chain = true)
    public static class Desulfurization {
        public String name;
        public desulfurizationComposition material;

        @Data
        @Accessors(chain = true)
        public static class desulfurizationComposition{
            public Double consumption;
        }
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
    public static class Heat {
        public String buy;
        public String outside;
        public String export;
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
