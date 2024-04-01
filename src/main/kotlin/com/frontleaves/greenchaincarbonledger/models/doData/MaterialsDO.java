package com.frontleaves.greenchaincarbonledger.models.doData;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    List<Materials> courses;
    // E固碳参数
    List<Materials> carbonSequestrations;
    // E脱硫参数
    List<Desulfurization> desulfurization;
    // E热力参数
    List<Material> heat;

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
     * E脱硫的种类和消耗量
     */
    @Data
    @Accessors(chain = true)
    public static class Desulfurization {
        public String name;
        public desulfurizationComposition material;

        @Data
        @Accessors(chain = true)
        public static class desulfurizationComposition {
            public Double consumption;
        }
    }

    @Data
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Material {
        public Double buy;
        public Double openingInv;
        public Double endingInv;
        public Double outside;
        public Double export;
    }
}
